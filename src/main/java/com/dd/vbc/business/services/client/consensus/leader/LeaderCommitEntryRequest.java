package com.dd.vbc.business.services.client.consensus.leader;

import com.dd.vbc.business.services.client.consensus.leader.events.CommitEntryEvent;
import com.dd.vbc.domain.AppendEntry;
import com.dd.vbc.domain.BlockChainMetadata;
import com.dd.vbc.domain.ConsensusServer;
import com.dd.vbc.domain.ConsensusState;
import com.dd.vbc.messageService.request.ConsensusRequest;
import com.dd.vbc.messageService.response.ConsensusResponse;
import com.dd.vbc.utils.ByteArrayUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


@Component
public class LeaderCommitEntryRequest implements ApplicationListener<CommitEntryEvent> {

    private static final Logger log = LoggerFactory.getLogger(LeaderCommitEntryRequest.class);

    @Override
    public void onApplicationEvent(CommitEntryEvent commitEntryRequest) {

        Consumer<byte[]> onSuccess = (byte[] bytes) ->  {
            ConsensusResponse<AppendEntry> consensusResponse = SerializationUtils.deserialize(bytes);
            AppendEntry entry = consensusResponse.getResponse();
            log.info("onSuccess - Received logCommit Message from Follower: "+entry.getIndex());

            if(entry.getCommitted()) {
                if (ConsensusState.getCommitEntryMap().get(entry.getIndex()) == null) {
                    List<AppendEntry> commitEntryList = new ArrayList<>();
                    commitEntryList.add(entry);
                    ConsensusState.getCommitEntryMap().put(entry.getIndex(), commitEntryList);
                } else {
                    ConsensusState.getCommitEntryMap().get(entry.getIndex()).add(entry);
                }
                if (!validateFollowerCommitResponse(entry)) {
                    //TODO Need to notify someone that a follower is not returning a valid blockchain hash
                    log.warn("Follower blockChain Hash != to Leader blockchain Hash: " + entry.toString());
                }

                //TODO Need to review this method to see if there is another way to determine follower commits
                if (ConsensusState.getCommitEntryMap().get(entry.getIndex()).size() == ConsensusState.getServerList().size()-1L) {
                    log.info("ALL FOLLOWERS COMMITTED FOR index: "+entry.getIndex());
                }
            }
        };
        Consumer<Throwable> onError = Throwable::getMessage;
        Runnable onCompletion = () -> { if(log.isDebugEnabled())
                                            log.debug("LeaderCommitEntryRequest onApplicationEvent Message Completed");};

        ConsensusRequest consensusRequest = new ConsensusRequest((AppendEntry) commitEntryRequest.getSource());
        byte[] requestBytes = SerializationUtils.serialize(consensusRequest);

        ConsensusState.getServerList().stream().forEach(server -> {
            if (!ConsensusServer.getId().equals(server.getId())) {
                log.info("sending commit message - onApplicationEvent, server Id: " + server.getId()+", index: "+consensusRequest.getAppendEntry().getIndex());
                ByteBuf requestByteBuf = Unpooled.wrappedBuffer(requestBytes);
                HttpClient.create()
                        .tcpConfiguration(tcpClient -> tcpClient.host(server.getHost()))
                        .port(server.getReactivePort())
                        .protocol(HttpProtocol.HTTP11)
                        .post()
                        .uri("/consensus/follower/commitEntry")
                        .send(Mono.just(requestByteBuf))
                        .responseContent()
                        .aggregate()
                        .asByteArray()
                        .subscribe(onSuccess, onError, onCompletion);
            }
        });
    }

    /**
     * This method determines if a follower is active or not. When a commit is issued by the leader, an AppendEntry
     * message is sent to all followers. The followers should return an AppendEntry object with the committed flag set to
     * true and with a valid blockChainHash. The leader compares its BlockChainHash with the follower. If any follower
     * didn't commit the electionTransaction or a follower blockchain hash doesn't agree with the majority hash, then
     * an error must be reported and the follower no longer receives messages from the leader.
     * @param appendEntry
     * @return
     */
    private boolean validateFollowerCommitResponse(AppendEntry appendEntry) {

        boolean result = false;
        if(BlockChainMetadata.getBlockChainHash()!=null && appendEntry.getBlockChainHash()!=null) {
            result = ByteArrayUtils.compareByteArray(BlockChainMetadata.getBlockChainHash(), appendEntry.getBlockChainHash());
        } else if(BlockChainMetadata.getBlockChainHash()==null) {
            if(log.isWarnEnabled()) log.warn("validateFollowerCommitResponse - BlockChainMetadata.getBlockChainHash(): "+BlockChainMetadata.getBlockChainHash());
        } else {
             if(log.isWarnEnabled()) log.warn("validateFollowerCommitResponse - appendEntry.getBlockChainHash(): "+appendEntry.getBlockChainHash());
        }
        return result;
    }
}
