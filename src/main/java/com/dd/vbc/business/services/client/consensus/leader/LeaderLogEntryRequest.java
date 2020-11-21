package com.dd.vbc.business.services.client.consensus.leader;

import com.dd.vbc.business.services.client.consensus.leader.events.CommitEntryEvent;
import com.dd.vbc.business.services.client.consensus.leader.events.LogEntryEvent;
import com.dd.vbc.business.services.server.blockchain.BlockChainService;
import com.dd.vbc.domain.AppendEntry;
import com.dd.vbc.domain.ConsensusServer;
import com.dd.vbc.domain.ConsensusState;
import com.dd.vbc.messageService.request.ConsensusRequest;
import com.dd.vbc.messageService.response.ConsensusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;


@Component
public class LeaderLogEntryRequest implements ApplicationListener<LogEntryEvent> {

    private static final Logger log = LoggerFactory.getLogger(LeaderLogEntryRequest.class);

    private WebClient webClient;
    @Autowired
    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    private BlockChainService blockChainService;

    @Autowired
    public void setBlockChainService(BlockChainService blockChainService) {
        this.blockChainService = blockChainService;
    }

    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.applicationEventPublisher = eventPublisher;
    }

    @Override
    public void onApplicationEvent(LogEntryEvent logEntryRequest) {

        Consumer<ConsensusResponse> onSuccess = (ConsensusResponse consensusResponse) ->  {

            if(log.isDebugEnabled()) log.debug("LogEntry Message Received from Follower - onApplicationEvent onSuccess: "+consensusResponse.toString());

            AppendEntry entry = (AppendEntry) consensusResponse.getResponse();
            if(entry.getLogged()) {
                if (ConsensusState.getLogEntryMap().get(entry.getIndex()) == null) {
                    List<AppendEntry> logEntryList = new ArrayList<>();
                    logEntryList.add(entry);
                    ConsensusState.getLogEntryMap().put(entry.getIndex(), logEntryList);
                } else {
                    ConsensusState.getLogEntryMap().get(entry.getIndex()).add(entry);
                }
                // If majority of followers has logged the election transaction, then notify followers to commit Tx.
                if ((double)(ConsensusState.getLogEntryMap().get(entry.getIndex()).size())>Math.floor(ConsensusState.getServerList().size()/2.0)) {
                    entry.setLog(false);
                    entry.setCommit(true);
                    if(ConsensusState.getCurrentIndex().get()!=ConsensusState.getCommitIndex().get()) {
                        blockChainService.followerCommitEntryResponse(entry).subscribe();
                        ConsensusState.setCommitIndex(new AtomicLong(ConsensusState.getCurrentIndex().get()));
                        entry.setServer(ConsensusServer.getServerInstance());
                        applicationEventPublisher.publishEvent(new CommitEntryEvent(entry));
                    }

                }
            } else {
                // TODO: need code to retransmit log entry command to the follower that failed to log entry.
            }
        };
        Consumer<Throwable> onError = Throwable::getMessage;
        Runnable onCompletion = () -> { if(log.isDebugEnabled()) log.debug("onApplicationEvent: Message Completed"); };

        ConsensusRequest consensusRequest = new ConsensusRequest((AppendEntry) logEntryRequest.getSource());

        ConsensusState.getServerList().stream().forEach(server -> {
            if (!ConsensusServer.getId().equals(server.getId())) {
                log.info("sending logEntry message - onApplicationEvent, server reactive port: " + server.getReactivePort()+", index: "+consensusRequest.getAppendEntry().getIndex());
                webClient.
                    post().
                    uri("http://localhost:"+server.getReactivePort()+"/consensus/follower/logEntry").
                    bodyValue(consensusRequest).
                    accept(MediaType.APPLICATION_JSON).
                    exchangeToMono(response -> response.bodyToMono(ConsensusResponse.class)).
                    subscribe(onSuccess, onError, onCompletion);
            }
        });
    }
}
