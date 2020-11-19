package com.dd.vbc.business.services.client.consensus.leader;

import com.dd.vbc.Application;
import com.dd.vbc.business.services.client.consensus.leader.LeaderNotificationRequest;
import com.dd.vbc.business.services.client.consensus.leader.events.LeaderNoticeEvent;
import com.dd.vbc.domain.Server;
import com.dd.vbc.enums.Request;
import com.dd.vbc.enums.ServerConsensusState;
import com.dd.vbc.messageService.httpClient.HttpMessage;
import com.dd.vbc.messageService.request.LeaderNoticeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes={Application.class, LeaderNotificationRequest.class, HttpMessage.class})
public class LeaderNotificationRequestTest {

    @Autowired
    LeaderNotificationRequest leaderNotificationRequest;

//    @Autowired
//    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    public void onApplicationEventTest() {

        Server server = new Server("A", "localhost", 8445, 61005, ServerConsensusState.Leader);
        LeaderNoticeRequest leaderNoticeRequest = new LeaderNoticeRequest(Request.LeaderNotification, server);
        LeaderNoticeEvent leaderNoticeEvent = new LeaderNoticeEvent(leaderNoticeRequest);
        leaderNotificationRequest.onApplicationEvent(leaderNoticeEvent);
//        applicationEventPublisher.publishEvent(new LeaderNoticeEvent(leaderNoticeRequest));

    }
}
