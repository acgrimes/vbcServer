package com.dd.vbc.business.services.client.consensus.scheduling;

import com.dd.vbc.domain.ConsensusServer;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Timer;

public class Scheduler {

    public Scheduler() {}

    @Autowired
    private ObjectFactory<LeaderHeartBeatTimerTask> leaderHeartBeatTimerTaskObjectFactory;

    @Autowired
    private ObjectFactory<FollowerHeartBeatTimeoutTask> followerHeartBeatTimeoutTaskObjectFactory;

    private Timer leaderHeartBeatTimer;
    private Timer followerHeartBeatTimer;
    private Timer leaderElectionTimeoutTimer;

    /**
     * This method starts a timer that on timeout a task is run that sends a HeartBeat message from the Leader
     * server to all the follower servers.
     */
    public synchronized void startLeaderHeartBeatTimer() {

        leaderHeartBeatTimer = new Timer();
        leaderHeartBeatTimer.scheduleAtFixedRate(leaderHeartBeatTimerTaskObjectFactory.getObject(), 0L, 2000L);
    }

    public synchronized void cancelLeaderHeartBeatTimer() {
        if(leaderHeartBeatTimer!=null) {
            leaderHeartBeatTimer.cancel();
//            leaderHeartBeatTimer.purge();
//            leaderHeartBeatTimer = null;
        }
    }

    /**
     * This method starts a follower timer used to determine if a leader election should occur,
     * that is, if this timer times out, if this server is in a follower state, it should promote
     * itself to candidate state and request votes.
     */
    public synchronized void startFollowerHeartBeatTimeoutTimer() {

        followerHeartBeatTimer = new Timer();
        if(ConsensusServer.getId().equals("A")) {
            followerHeartBeatTimer.schedule(followerHeartBeatTimeoutTaskObjectFactory.getObject(), 0L, 3000L);
        } else {
            followerHeartBeatTimer.schedule(followerHeartBeatTimeoutTaskObjectFactory.getObject(), 5000L, 3000L);
        }
    }

    public synchronized void cancelFollowerHeartBeatTimeoutTimer() {
        if(followerHeartBeatTimer!=null) {
            followerHeartBeatTimer.cancel();
//            followerHeartBeatTimer.purge();
//            followerHeartBeatTimer = null;
        }
    }


    public void startLeaderElectionTimeoutTimer() {

        leaderElectionTimeoutTimer = new Timer();
        leaderElectionTimeoutTimer.schedule(followerHeartBeatTimeoutTaskObjectFactory.getObject(), 0L, 3000L);
    }

    public void cancelLeaderElectionTimeoutTimer() {
        if(leaderElectionTimeoutTimer!=null) {
            leaderElectionTimeoutTimer.cancel();
//            leaderElectionTimeoutTimer.purge();
//            leaderElectionTimeoutTimer = null;
        }
    }
}
