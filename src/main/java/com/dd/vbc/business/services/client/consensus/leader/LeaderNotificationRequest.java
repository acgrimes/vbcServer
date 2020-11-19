package com.dd.vbc.business.services.client.consensus.leader;

import com.dd.vbc.business.services.client.consensus.leader.events.LeaderNoticeEvent;
import com.dd.vbc.domain.ConsensusState;
import com.dd.vbc.messageService.httpClient.HttpMessage;
import com.dd.vbc.messageService.request.LeaderNoticeRequest;
import com.dd.vbc.messageService.response.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * This class notifies the Proxy server that a new Leader has been created
 */
@Component
public class LeaderNotificationRequest implements ApplicationListener<LeaderNoticeEvent> {

    private static final Logger log = Logger.getLogger(LeaderNotificationRequest.class.getSimpleName());

    private HttpMessage httpMessage;

    @Autowired
    public void setHttpMessage(HttpMessage httpMessage) {
        this.httpMessage = httpMessage;
    }

    @Override
    public void onApplicationEvent(LeaderNoticeEvent leaderNoticeEvent) {

        httpMessage.setPort(ConsensusState.getVbcProxyServer().getHttpPort());
        httpMessage.setHost(ConsensusState.getVbcProxyServer().getHost());
        GeneralResponse generalResponse = httpMessage.postMessage((LeaderNoticeRequest) leaderNoticeEvent.getSource());
        log.info(generalResponse.toString());

    }
}
