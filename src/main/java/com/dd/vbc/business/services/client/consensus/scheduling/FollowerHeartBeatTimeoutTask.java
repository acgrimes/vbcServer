package com.dd.vbc.business.services.client.consensus.scheduling;

import com.dd.vbc.business.services.client.consensus.follower.FollowerTransitionToCandidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.TimerTask;
import java.util.logging.Logger;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FollowerHeartBeatTimeoutTask extends TimerTask {

    private static final Logger log = Logger.getLogger(FollowerHeartBeatTimeoutTask.class.getSimpleName());
    private FollowerTransitionToCandidate followerTransitionToCandidate;

    @Autowired
    public void setFollowerTransitionToCandidate(FollowerTransitionToCandidate followerTransitionToCandidate) {
        this.followerTransitionToCandidate = followerTransitionToCandidate;
    }

    public void run() {
        log.info("Follower timeout of Leader Heartbeat");
        followerTransitionToCandidate.transition();
    }

}
