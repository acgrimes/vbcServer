package com.dd.vbc.business.services.client.consensus.leader.events;

import com.dd.vbc.messageService.request.LeaderNoticeRequest;
import org.springframework.context.ApplicationEvent;

public class LeaderNoticeEvent extends ApplicationEvent {

    public LeaderNoticeEvent(LeaderNoticeRequest source) {
        super(source);
    }
}
