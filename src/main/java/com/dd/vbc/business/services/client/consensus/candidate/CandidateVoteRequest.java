package com.dd.vbc.business.services.client.consensus.candidate;

import com.dd.vbc.business.services.client.consensus.scheduling.Scheduler;
import com.dd.vbc.domain.ConsensusServer;
import com.dd.vbc.domain.ConsensusState;
import com.dd.vbc.domain.RequestLeaderVote;
import com.dd.vbc.enums.Request;
import com.dd.vbc.enums.ServerConsensusState;
import com.dd.vbc.messageService.request.LeaderVoteRequest;
import com.dd.vbc.messageService.response.LeaderVoteResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.function.Consumer;
import java.util.logging.Logger;

@Component
public class CandidateVoteRequest {

    private static final Logger log = Logger.getLogger(CandidateVoteRequest.class.getSimpleName());

    private Scheduler scheduler;

    @Autowired
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void leaderVoteRequest(LeaderVoteRequest leaderVoteRequest) {

        Consumer<byte[]> onSuccess = (byte[] bytes) -> {
            LeaderVoteResponse response = SerializationUtils.deserialize(bytes);
            log.info("RequestLeaderVote in onSuccess: "+response);
            if(response.getRequestLeaderVote().getGrantedVote()) {
                ConsensusServer.setLeaderVotes(ConsensusServer.getLeaderVotes()+1);
            }
            if((double)(ConsensusServer.getLeaderVotes())>=Math.ceil(ConsensusState.getServerList().size()/2.0)) {
                ConsensusServer.setState(ServerConsensusState.Leader);
                scheduler.cancelFollowerHeartBeatTimeoutTimer();
                scheduler.startLeaderHeartBeatTimer();
            }
            //TODO: send LeaderNotification to the Proxy server

        };
        Consumer<Throwable> onError = Throwable::getMessage;

        Runnable onCompletion = () -> {
            System.out.println("Message Completed");

        };

        log.info("Sending Candidate Vote Request");

        byte[] requestBytes = SerializationUtils.serialize((leaderVoteRequest));

        ConsensusState.getServerList().stream().forEach(server -> {
            log.info("ConsensusServer Id: "+ConsensusServer.getId()+", server Id: "+server.getId());
            if(!ConsensusServer.getId().equals(server.getId())) {
                ByteBuf requestByteBuf = Unpooled.wrappedBuffer(requestBytes);
                HttpClient.create()
                        .tcpConfiguration(tcpClient -> tcpClient.host(server.getHost()))
                        .port(server.getReactivePort())
                        .post()
                        .uri("/consensus/follower/candidateVoteRequest")
                        .send(Mono.just(requestByteBuf))
                        .responseContent()
                        .aggregate()
                        .asByteArray()
                        .subscribe(onSuccess, onError, onCompletion);
            }
        });
    }

    /**
     *
     */
    public void sendCandidateLeaderVoteRequest() {

        RequestLeaderVote requestLeaderVote = new RequestLeaderVote(ConsensusServer.getHost(),
                                                                    ConsensusServer.getReactivePort(),
                                                                    ConsensusState.getCurrentIndex(),
                                                                    ConsensusState.getLastCurrentIndex(),
                                                                    ConsensusState.getCurrentTerm(),
                                                                    ConsensusState.getLastCurrentTerm(),
                                                                    Boolean.FALSE);
        LeaderVoteRequest leaderVoteRequest = new LeaderVoteRequest(Request.LeaderVote, requestLeaderVote);
        leaderVoteRequest(leaderVoteRequest);
    }

    public static final void main(String[] args) {
        int a = 2;
        int b = 3;
        if((double)(a)>=Math.ceil(b/2.0)) {
            System.out.println("True, 2 >= 2");
        }
    }
}
