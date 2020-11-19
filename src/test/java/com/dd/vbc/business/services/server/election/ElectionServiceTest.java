package com.dd.vbc.business.services.server.election;

import com.dd.vbc.business.services.server.election.ElectionService;
import com.dd.vbc.dao.consensus.ConsensusLogDao;
import com.dd.vbc.dao.consensus.repository.ConsensusLogRepository;
import com.dd.vbc.db.mongo.MongoConfig;
import com.dd.vbc.domain.ConsensusServer;
import com.dd.vbc.domain.ConsensusState;
import com.dd.vbc.enums.ReturnCode;
import com.dd.vbc.messageService.request.ElectionRequest;
import com.dd.vbc.messageService.response.GeneralResponse;
import com.dd.vbc.utils.BuildElectionRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.util.concurrent.atomic.AtomicLong;

import static com.dd.vbc.enums.ServerConsensusState.Follower;

@SpringBootTest(classes={ElectionService.class, MongoConfig.class, ConsensusLogDao.class, ApplicationEventPublisher.class, ConsensusLogRepository.class})
public class ElectionServiceTest {

    @Autowired
    private ElectionService electionService;

    @Test
    public void electionTransactionResponseTest() {

        ConsensusServer.setId("A");
        ConsensusServer.setHost("localhost");
        ConsensusServer.setHttpPort(8444);
        ConsensusServer.setReactivePort(61005);
        ConsensusServer.setState(Follower);

        ConsensusState.setCurrentIndex(new AtomicLong(0L));
        ConsensusState.setCurrentTerm(new AtomicLong(21L));

        ElectionRequest electionRequest = BuildElectionRequest.build();
        GeneralResponse response = electionService.electionTransactionResponse(electionRequest).block();
        Assert.assertEquals(ReturnCode.SUCCESS, response.getReturnCode());
    }
}
