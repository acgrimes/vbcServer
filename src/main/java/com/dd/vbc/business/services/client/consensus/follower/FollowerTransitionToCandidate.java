package com.dd.vbc.business.services.client.consensus.follower;

import com.dd.vbc.business.services.client.consensus.candidate.CandidateVoteRequest;
import com.dd.vbc.domain.ConsensusServer;
import com.dd.vbc.enums.ServerConsensusState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class FollowerTransitionToCandidate {

    private static final Logger log = Logger.getLogger(FollowerTransitionToCandidate.class.getSimpleName());

    @Autowired
    private CandidateVoteRequest candidateVoteRequest;

    public void transition() {
        log.info("Follower Transitioning to Candidate");
        ConsensusServer.setState(ServerConsensusState.Candidate);
        candidateVoteRequest.sendCandidateLeaderVoteRequest();

    }
}