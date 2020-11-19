package com.dd.vbc.business.services.client.consensus.leader.events;

import com.dd.vbc.domain.AppendEntry;
import org.springframework.context.ApplicationEvent;

public class LogEntryEvent extends ApplicationEvent {

    public LogEntryEvent(AppendEntry source) {
        super(source);
    }
}
