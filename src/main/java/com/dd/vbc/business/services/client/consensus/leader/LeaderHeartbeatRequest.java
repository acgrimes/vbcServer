package com.dd.vbc.business.services.client.consensus.leader;

import com.dd.vbc.domain.AppendEntry;
import com.dd.vbc.domain.ConsensusServer;
import com.dd.vbc.domain.ConsensusState;
import com.dd.vbc.domain.Server;
import com.dd.vbc.messageService.request.ElectionRequest;
import com.dd.vbc.messageService.request.HeartBeatRequest;
import com.dd.vbc.messageService.response.HeartBeatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;


@Component
public class LeaderHeartbeatRequest {

    private static final Logger log = LoggerFactory.getLogger(LeaderHeartbeatRequest.class);

    private WebClient webClient;
    @Autowired
    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public void heartbeatRequest(HeartBeatRequest heartBeatRequest) {

        Consumer<HeartBeatResponse> onSuccess = (HeartBeatResponse response) -> {
            log.debug("heartbeat response follower in onSuccess: "+response.toString());
        };
        Consumer<Throwable> onError = Throwable::getMessage;

        Runnable onCompletion = () -> log.debug("heartbeat message response from follower complete");

        ConsensusState.getServerList().stream().forEach(server -> {
            if (!ConsensusServer.getId().equals(server.getId())) {
                log.info("sending commit message - onApplicationEvent, server Id: " + server.getId()+", index: "+heartBeatRequest.getAppendEntry().getIndex());
                webClient.
                    post().
                    uri(server.getReactivePort()+"/consensus/follower/heartbeat").
                    bodyValue(heartBeatRequest).
                    accept(MediaType.APPLICATION_JSON).
                    exchangeToMono(response -> response.bodyToMono(HeartBeatResponse.class)).
                    subscribe(onSuccess, onError, onCompletion);
            }
        });

    }

    private HeartBeatRequest buildLeaderHeartbeatRequest() {

        return null;

    }

    /**
     *  This method builds the Leader Heartbeat message sent to all follower servers:
     */
    public void sendLeaderHeartbeatRequest() {

        ElectionRequest electionRequest = new ElectionRequest();
        AppendEntry appendEntry = new AppendEntry(new Server(ConsensusServer.getId(),
                                                             ConsensusServer.getHost(),
                                                             ConsensusServer.getHttpPort(),
                                                             ConsensusServer.getReactivePort(),
                                                             ConsensusServer.getState()),
                                                ConsensusState.getCurrentIndex().get(),
                                                ConsensusState.getCurrentTerm().get(),
                                                electionRequest.getElectionTransaction());
        HeartBeatRequest heartBeatRequest = new HeartBeatRequest(appendEntry);
        heartbeatRequest(heartBeatRequest);
    }

    /**
     *
     * @param followerAppendEntry
     * @return
     */
    private Long[] determineFollowerIndexTermMismatch(AppendEntry followerAppendEntry) {


        return null;
    }

    /**
     *
     * @param indexes
     */
    private void createFollowerIndexUpdateEvent(Long[] indexes) {


    }
}
