package com.dd.vbc.business.services.client.consensus.scheduling;

import com.dd.vbc.business.services.client.consensus.leader.LeaderHeartbeatRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.TimerTask;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LeaderHeartBeatTimerTask extends TimerTask {

    @Autowired
    private LeaderHeartbeatRequest leaderHeartbeatRequest;

    public void run() {
        leaderHeartbeatRequest.sendLeaderHeartbeatRequest();
    }

}
