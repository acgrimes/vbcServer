package com.dd.vbc.business.services.server.consensus.follower;

import com.dd.vbc.business.services.server.blockchain.BlockChainService;
import com.dd.vbc.business.services.server.consensus.ConsensusService;
import com.dd.vbc.enums.ReturnCode;
import com.dd.vbc.messageService.request.ConsensusRequest;
import com.dd.vbc.messageService.request.HeartBeatRequest;
import com.dd.vbc.messageService.request.LeaderVoteRequest;
import com.dd.vbc.messageService.response.ConsensusResponse;
import com.dd.vbc.messageService.response.HeartBeatResponse;
import com.dd.vbc.messageService.response.LeaderVoteResponse;
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

        log.debug("Entering heartbeatFollowerResponse()");

        return ServerResponse.
                ok().
                contentType(MediaType.APPLICATION_JSON).
                body(BodyInserters.fromProducer(serverRequest.bodyToMono(HeartBeatRequest.class).
                                flatMap(hr -> consensusService.followerHeartbeatResponse(hr.getAppendEntry())).
                                flatMap(ae -> Mono.just(new HeartBeatResponse(ae))).
                                doOnSuccess(ae -> { if(log.isDebugEnabled()) log.debug("heartbeatFollowerResponse(): "+ae.toString());}),
                                                        HeartBeatResponse.class)).
                                doOnError(em -> log.error("Error: "+em.getLocalizedMessage()));
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
                                flatMap(ae -> Mono.just(new ConsensusResponse(HttpStatus.OK, ReturnCode.SUCCESS, ae))).
                                doOnSuccess(cr -> {if(log.isDebugEnabled()) log.debug("logEntryFollowerResponse(): "+cr.toString());}).
                                doOnError(ex -> log.error("Error: "+ex.getLocalizedMessage()))),
                                    ConsensusResponse.class)).
                                doOnError(em -> log.error("Error: "+em.getLocalizedMessage()));
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
                                flatMap(ae -> Mono.just(new ConsensusResponse(HttpStatus.OK, ReturnCode.SUCCESS, ae))).
                                doOnSuccess(cr -> {if(log.isDebugEnabled()) log.debug("commitEntryFollowerResponse(): "+cr.toString());})),
                                    ConsensusResponse.class));
    }

    public Mono<ServerResponse> candidateVoteRequestResponse(ServerRequest serverRequest) {

        log.info("candidateVoteRequestResponse()");

        return ServerResponse.
                ok().
                contentType(MediaType.APPLICATION_JSON).
                body(BodyInserters.fromProducer(serverRequest.bodyToMono(LeaderVoteRequest.class).
                                flatMap(req -> consensusService.candidateVoteRequest(req.getRequestLeaderVote()).
                                flatMap(rlv -> Mono.just(new LeaderVoteResponse(ReturnCode.SUCCESS, rlv))).
                                doOnSuccess(res -> {if(log.isDebugEnabled()) log.debug("candidateVoteRequestResponse(): "+res.toString());})),
                                    LeaderVoteResponse.class));


    }
}
