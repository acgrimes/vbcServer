package com.dd.vbc.messageService.webflux;

import com.dd.vbc.business.services.server.consensus.follower.FollowerResponseHandler;
import com.dd.vbc.business.services.server.consensus.follower.GreetingHandler;
import com.dd.vbc.business.services.server.election.leader.ElectionTransactionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
@EnableWebFlux
public class RouterFunctionConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RouterFunctionConfiguration.class);

    private FollowerResponseHandler followerResponseHandler;

    @Autowired
    public void setFollowerResponseHandler(FollowerResponseHandler followerResponseHandler) {
        this.followerResponseHandler = followerResponseHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> electionTransaction(ElectionTransactionHandler electionTransactionHandler) {
        if(log.isDebugEnabled())
            log.debug("entering electionTransaction()");

        return route(RequestPredicates.POST("/election/transaction").
                and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                electionTransactionHandler::electionTransactionResponse);
    }

    @Bean
    public RouterFunction<ServerResponse> followerHeartbeat(FollowerResponseHandler followerResponseHandler) {
        if(log.isDebugEnabled())
            log.debug("entering followerHeartbeat");

        return route(RequestPredicates.POST("/consensus/heartbeat").
                and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                followerResponseHandler::heartbeatFollowerResponse);
    }

    @Bean
    public RouterFunction<ServerResponse> followerLogEntry(FollowerResponseHandler followerResponseHandler) {
        if(log.isDebugEnabled())
            log.debug("entering followerLogEntry()");

        return route(RequestPredicates.POST("/consensus/logEntry").
                and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                followerResponseHandler::logEntryFollowerResponse);
    }

    @Bean
    public RouterFunction<ServerResponse> followerCommitEntry(FollowerResponseHandler followerResponseHandler) {
        if(log.isDebugEnabled())
            log.debug("entering followerCommitEntry()");

        return route(RequestPredicates.POST("/consensus/commitEntry").
                and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                followerResponseHandler::commitEntryFollowerResponse);
    }

    @Bean
    public RouterFunction<ServerResponse> greeting(GreetingHandler greetingHandler) {

        return RouterFunctions
                .route(RequestPredicates.POST("/hello").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), greetingHandler::hello);
    }

}
