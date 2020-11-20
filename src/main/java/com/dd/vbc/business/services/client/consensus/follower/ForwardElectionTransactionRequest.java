package com.dd.vbc.business.services.client.consensus.follower;

import com.dd.vbc.business.services.client.consensus.follower.events.ForwardElectionTransactionEvent;
import com.dd.vbc.domain.AppendEntry;
import com.dd.vbc.domain.ConsensusState;
import com.dd.vbc.domain.Server;
import com.dd.vbc.messageService.request.ElectionRequest;
import com.dd.vbc.messageService.response.GeneralResponse;
import com.dd.vbc.messageService.webflux.WebClientConfiguration;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class ForwardElectionTransactionRequest implements ApplicationListener<ForwardElectionTransactionEvent> {

    private static final Logger log = Logger.getLogger(ForwardElectionTransactionRequest.class.getSimpleName());
    private final WebClient webClient = WebClientConfiguration.getWebClient();

    @Override
    public void onApplicationEvent(ForwardElectionTransactionEvent forwardElectionTransactionEvent) {

        Consumer<GeneralResponse> onSuccess = (GeneralResponse response) -> {
            log.info("Response in onSuccess: " + response);
        };
        Consumer<Throwable> onError = Throwable::getMessage;
        Runnable onCompletion = () -> System.out.println("Forwarding ElectionTransaction Message Completed");

        ElectionRequest electionRequest = (ElectionRequest) forwardElectionTransactionEvent.getSource();

        webClient.
            post().
            uri("/election/transaction").
            bodyValue(electionRequest).
            accept(MediaType.APPLICATION_JSON).
            exchangeToMono(response -> response.bodyToMono(GeneralResponse.class)).
            subscribe(onSuccess, onError, onCompletion);
    }
}
