package com.dd.vbc.business.services.client.consensus.leader;

import com.dd.vbc.domain.AppendEntry;
import com.dd.vbc.domain.ConsensusServer;
import com.dd.vbc.domain.ConsensusState;
import com.dd.vbc.domain.Server;
import com.dd.vbc.enums.Request;
import com.dd.vbc.messageService.request.ElectionRequest;
import com.dd.vbc.messageService.request.HeartBeatRequest;
import com.dd.vbc.messageService.response.GeneralResponse;
import com.dd.vbc.messageService.response.HeartBeatResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;

import java.util.function.Consumer;


@Component
public class LeaderHeartbeatRequest {

    private static final Logger log = LoggerFactory.getLogger(LeaderHeartbeatRequest.class);

    public void heartbeatRequest(HeartBeatRequest heartBeatRequest) {

        Consumer<byte[]> onSuccess = (byte[] response) -> {
            HeartBeatResponse heartBeatResponse = SerializationUtils.deserialize(response);
            log.debug("heartbeat response follower in onSuccess: "+heartBeatResponse.toString());

        };
        Consumer<Throwable> onError = Throwable::getMessage;

        Runnable onCompletion = () -> log.debug("heartbeat message response from follower complete");

        byte[] requestBytes = SerializationUtils.serialize(heartBeatRequest);

        ConsensusState.getServerList().stream().forEach(
            server -> {
                if(!ConsensusServer.getId().equals(server.getId())) {
                    log.info("Sending Leader HeartBeat to follower: "+server.getId());
                    ByteBuf requestByteBuf = Unpooled.wrappedBuffer(requestBytes);
                    HttpClient.create()
                              .tcpConfiguration(tcpClient -> tcpClient.host(server.getHost()))
                              .port(server.getReactivePort())
                              .protocol(HttpProtocol.HTTP11)
                              .post()
                              .uri("/consensus/follower/heartbeat")
                              .send(Mono.just(requestByteBuf))
                              .responseContent()
                              .aggregate()
                              .asByteArray()
                              .subscribe(onSuccess, onError, onCompletion);
                }
            }
        );
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
