package com.dd.vbc.business.services.client.consensus.scheduling;

import com.dd.vbc.business.services.client.consensus.candidate.CandidateVoteRequest;
import com.dd.vbc.business.services.client.consensus.follower.FollowerTransitionToCandidate;
import com.dd.vbc.business.services.server.blockchain.BlockChainService;
import com.dd.vbc.business.services.server.consensus.ConsensusService;
import com.dd.vbc.business.services.server.consensus.follower.FollowerResponseHandler;
import com.dd.vbc.business.services.server.election.leader.ElectionTransactionHandler;
import com.dd.vbc.domain.ConsensusServer;
import com.dd.vbc.enums.ServerConsensusState;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {ScheduleConfiguration.class,
                            FollowerHeartBeatTimeoutTask.class,
                            FollowerTransitionToCandidate.class,
                            CandidateVoteRequest.class,
                            ElectionTransactionHandler.class,
                            FollowerResponseHandler.class,
                            ConsensusService.class,
                            BlockChainService.class})
public class SchedulerTest {

    @MockBean
    private ElectionTransactionHandler electionTransactionHandler;

    @MockBean
    private ConsensusService consensusService;

    @MockBean
    private BlockChainService blockChainService;

    @Autowired
    private Scheduler scheduler;

    @Test
    public void testFollowerHeartBeatTimeoutTaskTest() {

        ConsensusServer.setId("A");
        ConsensusServer.setHost("localhost");
        ConsensusServer.setHttpPort(8444);
        ConsensusServer.setReactivePort(61005);
        ConsensusServer.setState(ServerConsensusState.Follower);

        scheduler.startFollowerHeartBeatTimeoutTimer();
        try {
            Thread.sleep(3000);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        Assert.assertEquals(ConsensusServer.getState(), ServerConsensusState.Candidate);

    }
}
