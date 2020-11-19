package com.dd.vbc.business.services.client.consensus.follower.events;

import com.dd.vbc.messageService.request.ElectionRequest;
import org.springframework.context.ApplicationEvent;

public class ForwardElectionTransactionEvent extends ApplicationEvent {

    public ForwardElectionTransactionEvent(ElectionRequest source) {
        super(source);
    }
}
