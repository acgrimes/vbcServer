package com.dd.vbc.business.services.server.consensus;

import com.dd.vbc.business.services.client.consensus.scheduling.Scheduler;
import com.dd.vbc.dao.consensus.ConsensusLogDao;
import com.dd.vbc.domain.AppendEntry;
import com.dd.vbc.domain.ConsensusServer;
import com.dd.vbc.domain.RequestLeaderVote;
import com.dd.vbc.enums.ServerConsensusState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
public class ConsensusService {

    private static final Logger log = LoggerFactory.getLogger(ConsensusService.class);

    @Autowired
    private ConsensusLogDao consensusLogDao;

    @Autowired
    private Scheduler scheduler;

    /**
     * This is the follower response to a receiving a Leader heartbeat message.
     * @param leaderAppendEntry
     * @return
     */
    public Mono<AppendEntry> followerHeartbeatResponse(AppendEntry leaderAppendEntry) {

        log.info(leaderAppendEntry.toString());

        scheduler.cancelFollowerHeartBeatTimeoutTimer();
        scheduler.startFollowerHeartBeatTimeoutTimer();

        // if AppendEntry>>ConsensusLog is not in db then follower will create the log entry and return
        // but if AppendEntry>>ConsensusLog is in db, then client gets a valid Mono<AppendEntry> returned.
        return consensusLogDao.get(leaderAppendEntry.getIndex()).
            switchIfEmpty(consensusLogDao.create(leaderAppendEntry)).
            flatMap(cl ->
                 Mono.just(new AppendEntry(ConsensusServer.getServerInstance(),
                                                    cl.getLogIndex(),
                                                    cl.getLogTerm(),
                                                    leaderAppendEntry.getElectionTransaction()))).
               doOnSuccess(ap -> log.debug("followerHeartbeatResponse: "+ap.toString())).
               doOnError(Throwable::printStackTrace);
    }

    public Mono<AppendEntry> followerLogEntryResponse(AppendEntry leaderAppendEntry) {
        log.debug("method - followerLogEntryResponse: "+leaderAppendEntry.toString());
        return consensusLogDao.create(leaderAppendEntry).
            flatMap(cl -> {
                AppendEntry ae = new AppendEntry(ConsensusServer.getServerInstance(),
                                                cl.getLogIndex(),
                                                cl.getLogTerm(),
                                                leaderAppendEntry.getElectionTransaction());
                ae.setLogged(Boolean.TRUE);
                ae.setvToken(leaderAppendEntry.getvToken());
                return Mono.just(ae);
            })
            .doOnSuccess(ap -> log.debug("followerLogEntryResponse: "+ap.toString()))
            .doOnError(Throwable::printStackTrace);

    }

    public Mono<RequestLeaderVote> candidateVoteRequest(RequestLeaderVote requestLeaderVote) {

        boolean approveElection = false;
        if(ConsensusServer.getState()==ServerConsensusState.Follower) {
            approveElection = true;
            scheduler.cancelFollowerHeartBeatTimeoutTimer();
        }

        return Mono.just(new RequestLeaderVote(ConsensusServer.getHost(),
                                                ConsensusServer.getReactivePort(),
                                                requestLeaderVote.getLogIndex(),
                                                requestLeaderVote.getLogTerm(),
                                                requestLeaderVote.getLastLogIndex(),
                                                requestLeaderVote.getLastLogTerm(),
                                                approveElection));
    }
}
