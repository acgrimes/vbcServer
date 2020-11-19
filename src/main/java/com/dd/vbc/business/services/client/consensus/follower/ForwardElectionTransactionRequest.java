package com.dd.vbc.business.services.client.consensus.follower;

import com.dd.vbc.business.services.client.consensus.follower.events.ForwardElectionTransactionEvent;
import com.dd.vbc.domain.AppendEntry;
import com.dd.vbc.domain.ConsensusState;
import com.dd.vbc.domain.Server;
import com.dd.vbc.messageService.request.ElectionRequest;
import com.dd.vbc.messageService.response.GeneralResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.context.ApplicationListener;
import reactor.core.publisher.Mono;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class ForwardElectionTransactionRequest implements ApplicationListener<ForwardElectionTransactionEvent> {

    private static final Logger log = Logger.getLogger(ForwardElectionTransactionRequest.class.getSimpleName());

    @Override
    public void onApplicationEvent(ForwardElectionTransactionEvent forwardElectionTransactionEvent) {

        Consumer<byte[]> onSuccess = (byte[] bytes) ->  {
            GeneralResponse response = SerializationUtils.deserialize(bytes);
            log.info("Response in onSuccess: "+response);

        };
        Consumer<Throwable> onError = Throwable::getMessage;
        Runnable onCompletion = () -> System.out.println("Forwarding ElectionTransaction Message Completed");

        byte[] requestBytes = SerializationUtils.serialize((ElectionRequest) forwardElectionTransactionEvent.getSource());
        ByteBuf requestByteBuf = Unpooled.copiedBuffer(requestBytes);

        HttpClient.create()
                .tcpConfiguration(tcpClient -> tcpClient.host("localhost"))
                .port(61005)
                .protocol(HttpProtocol.HTTP11)
                .post()
                .uri("/election/transaction")
                .send(Mono.just(requestByteBuf))
                .responseContent()
                .aggregate()
                .asByteArray()
                .subscribe(onSuccess, onError, onCompletion);
    }

}
