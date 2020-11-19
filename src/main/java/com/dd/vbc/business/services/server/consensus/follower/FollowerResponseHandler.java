package com.dd.vbc.business.services.server.consensus.follower;

import com.dd.vbc.business.services.server.blockchain.BlockChainService;
import com.dd.vbc.business.services.server.consensus.ConsensusService;
import com.dd.vbc.domain.AppendEntry;
import com.dd.vbc.enums.ReturnCode;
import com.dd.vbc.messageService.request.ConsensusRequest;
import com.dd.vbc.messageService.request.HeartBeatRequest;
import com.dd.vbc.messageService.request.LeaderVoteRequest;
import com.dd.vbc.messageService.response.ConsensusResponse;
import com.dd.vbc.messageService.response.HeartBeatResponse;
import com.dd.vbc.messageService.response.LeaderVoteResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Component
public class FollowerResponseHandler {

    private Logger log = LoggerFactory.getLogger(FollowerResponseHandler.class);

    @Autowired
    private ConsensusService consensusService;

    @Autowired
    private BlockChainService blockChainService;

    /**
     *
     * @param serverRequest - HeartBeatRequest
     * @return - HeartBeatResponse
     */
    public Mono<ServerResponse> heartbeatFollowerResponse(ServerRequest serverRequest) {

        return ServerResponse.
                ok().
                contentType(MediaType.APPLICATION_JSON).
                body(BodyInserters.fromProducer(serverRequest.bodyToMono(HeartBeatRequest.class).
                                flatMap(hr -> consensusService.followerHeartbeatResponse(hr.getAppendEntry())).
                                flatMap(ae -> Mono.just(new HeartBeatResponse(ae))).
                                doOnSuccess(ae -> { if(log.isDebugEnabled()) log.debug("heartbeatFollowerResponse: "+ae.toString());}),
                                                        HeartBeatResponse.class));
    }

    /**
     *
     * @param serverRequest
     * @return
     */
    public Mono<ServerResponse> logEntryFollowerResponse(ServerRequest serverRequest) {

        return ServerResponse.
                ok().
                contentType(MediaType.APPLICATION_JSON).
                body(BodyInserters.fromProducer(serverRequest.bodyToMono(ConsensusRequest.class).
                                flatMap(p -> consensusService.followerLogEntryResponse(p.getAppendEntry()).
                                flatMap(ae -> Mono.just(new ConsensusResponse<AppendEntry>(HttpStatus.OK, ReturnCode.SUCCESS, ae))).
                                doOnSuccess(cr -> {if(log.isDebugEnabled()) log.debug("logEntryFollowerResponse(): "+cr.toString());})),
                                    ConsensusResponse.class));
    }

    /**
     *
     * @param serverRequest
     * @return
     */
    public Mono<ServerResponse> commitEntryFollowerResponse(ServerRequest serverRequest) {

        return ServerResponse.
                ok().
                contentType(MediaType.APPLICATION_JSON).
                body(BodyInserters.fromProducer(serverRequest.bodyToMono(ConsensusRequest.class).
                                flatMap(p -> blockChainService.followerCommitEntryResponse(p.getAppendEntry()).
                                flatMap(ae -> Mono.just(new ConsensusResponse<AppendEntry>(HttpStatus.OK, ReturnCode.SUCCESS, ae))).
                                doOnSuccess(cr -> {if(log.isDebugEnabled()) log.debug("commitEntryFollowerResponse(): "+cr.toString());})),
                                    ConsensusResponse.class));
    }

    public Mono<ByteBuf> candidateVoteRequestResponse(ByteBuf byteBuf) {

        byte[] bytes = null;
        LeaderVoteRequest leaderVoteRequest = null;
        try {
            if (byteBuf.hasArray()) {
                bytes = byteBuf.array();
            } else {
                bytes = new byte[byteBuf.readableBytes()];
                byteBuf.duplicate().readBytes(bytes);
            }
            leaderVoteRequest = SerializationUtils.deserialize(bytes);
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            byteBuf.release();
        }
        log.info("class - FollowerResponseHandler, method - candidateVoteRequestResponse");
        return consensusService.candidateVoteRequest(leaderVoteRequest.getRequestLeaderVote()).
            flatMap(rlv -> {
                LeaderVoteResponse response = new LeaderVoteResponse(ReturnCode.SUCCESS, rlv);
                return Mono.just(Unpooled.copiedBuffer(SerializationUtils.serialize(response)));
            }).
            doOnSuccess(System.out::println);
    }
}
