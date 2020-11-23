package com.dd.vbc.business.services.client.consensus.leader;

import com.dd.vbc.business.services.server.blockchain.BlockChainService;
import com.dd.vbc.domain.AppendEntry;
import com.dd.vbc.domain.ConsensusServer;
import com.dd.vbc.domain.ConsensusState;
import com.dd.vbc.messageService.request.ConsensusRequest;
import com.dd.vbc.messageService.response.ConsensusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;


@Component
public class LeaderLogEntryRequest implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(com.dd.vbc.business.services.client.consensus.leader.LeaderLogEntryRequest.class);

    private AppendEntry appendEntry;
    public void setAppendEntry(AppendEntry appendEntry) {
        this.appendEntry = appendEntry;
    }

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

    private LeaderCommitEntryRequest leaderCommitEntryRequest;
    @Autowired
    public void setLeaderCommitEntryRequest(LeaderCommitEntryRequest leaderCommitEntryRequest) {
        this.leaderCommitEntryRequest = leaderCommitEntryRequest;
    }

    private ThreadPoolExecutor executor;
    @Autowired
    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void run() {

        log.debug("entering run()");

        Consumer<ConsensusResponse> onSuccess = (ConsensusResponse consensusResponse) ->  {

            if(log.isDebugEnabled()) log.debug("LogEntry Message Received from Follower - method run() onSuccess: "+consensusResponse.toString());

            AppendEntry entry = consensusResponse.getResponse();
            if(log.isDebugEnabled()) log.debug("LogEntry Message Received from Follower - method run() onSuccess: "+entry.toString());

            if(entry.getLogged()) {
                log.debug("entry logged");
                if (ConsensusState.getLogEntryMap().get(entry.getIndex()) == null) {
                    log.debug("entry logged, Map null");
                    List<AppendEntry> logEntryList = new ArrayList<>();
                    logEntryList.add(entry);
                    ConsensusState.getLogEntryMap().put(entry.getIndex(), logEntryList);
                } else {
                    log.debug("entry logged, Map not null");
                    ConsensusState.getLogEntryMap().get(entry.getIndex()).add(entry);
                }
                // If majority of followers has logged the election transaction, then notify followers to commit Tx.
                if ((double)(ConsensusState.getLogEntryMap().get(entry.getIndex()).size())>Math.floor((double)(ConsensusState.getServerList().size())/2.0)) {
                    log.debug("majority followers logged");
                    entry.setLog(false);
                    entry.setCommit(true);
                    if(ConsensusState.getLeaderCommitList().get(entry.getIndex().intValue())==Boolean.FALSE) {
                        log.debug("Majority of followers has Logged Entry");
                        Consumer<AppendEntry> oSuccess = (AppendEntry appendEntry) -> {
                            ConsensusState.getLeaderCommitList().set(appendEntry.getIndex().intValue(), Boolean.TRUE);
                            appendEntry.setServer(ConsensusServer.getServerInstance());
                            leaderCommitEntryRequest.setAppendEntry(entry);
                            executor.execute(leaderCommitEntryRequest);
                        };
                        Consumer<Throwable> oError = Throwable::getMessage;
                        Runnable oCompletion = () -> { if(log.isDebugEnabled()) log.debug("onApplicationEvent: Message Completed"); };

                        blockChainService.followerCommitEntryResponse(entry).
                            subscribe(oSuccess, oError, oCompletion);
                    }
                } else {
                    log.debug("majority followers not logged");
                    ConsensusState.getLeaderCommitList().set(entry.getIndex().intValue(), Boolean.FALSE);
                }
                log.debug("ConsensusState LeaderCommitList at "+entry.getIndex()+" is "+ConsensusState.getLeaderCommitList().get(entry.getIndex().intValue()));
            } else {
                log.warn("follower AppendEntry not logged!");
                // TODO: need code to retransmit log entry command to the follower that failed to log entry.
            }
        };
        Consumer<Throwable> onError = Throwable::getMessage;
        Runnable onCompletion = () -> { if(log.isDebugEnabled()) log.debug("onApplicationEvent: Message Completed"); };

        ConsensusRequest consensusRequest = new ConsensusRequest(appendEntry);

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
