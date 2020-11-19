package com.dd.vbc.business.services.client.consensus.leader.events;

import com.dd.vbc.domain.AppendEntry;
import org.springframework.context.ApplicationEvent;

public class CommitEntryEvent extends ApplicationEvent {

    public CommitEntryEvent(AppendEntry source) {
        super(source);
    }
}
