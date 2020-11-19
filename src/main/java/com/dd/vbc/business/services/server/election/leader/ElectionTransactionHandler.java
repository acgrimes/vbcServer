package com.dd.vbc.business.services.server.election.leader;

import com.dd.vbc.business.services.server.election.ElectionService;
import com.dd.vbc.messageService.request.ElectionRequest;
import com.dd.vbc.messageService.response.GeneralResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ElectionTransactionHandler {

    private static final Logger log = LoggerFactory.getLogger(ElectionTransactionHandler.class);

    public ElectionService electionService;

    @Autowired
    public void setElectionService(ElectionService electionService) {
        this.electionService = electionService;
    }

    /**
     *
     * @param serverRequest
     * @return
     */
    public Mono<ServerResponse> electionTransactionResponse(ServerRequest serverRequest) {

        return ServerResponse.
                ok().
                contentType(MediaType.APPLICATION_JSON).
                body(BodyInserters.fromProducer(serverRequest.bodyToMono(ElectionRequest.class).
                                flatMap(p -> electionService.electionTransactionResponse(p)).
                                doOnSuccess(gr -> { if(log.isDebugEnabled()) log.debug("electionTransactionResponse(): "+gr.toString());}), GeneralResponse.class));
    }

}
