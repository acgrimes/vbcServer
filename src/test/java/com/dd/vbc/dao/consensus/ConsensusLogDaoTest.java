package com.dd.vbc.dao.consensus;

import com.dd.vbc.db.mongo.MongoConfig;
import com.dd.vbc.domain.*;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

@ExtendWith(SpringExtension.class)
// ApplicationContext will be loaded from AppConfig and TestConfig
@ContextConfiguration(classes = {MongoConfig.class, ConsensusLogDao.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConsensusLogDaoTest {

    private static final Logger log = Logger.getLogger(ConsensusLogDaoTest.class.getSimpleName());

    @Autowired
    private ConsensusLogDao consensusLogDao;

    @Test
    @Order(5)
    public void consensusLogDeleteTest() throws InterruptedException {
        ConsensusLog consensusLog = new ConsensusLog(1L, 1L, buildElectionTransaction());
        StepVerifier.create(consensusLogDao.delete(consensusLog).
                doOnNext(System.out::println)).
                expectNextCount(1).
                verifyComplete();
        Thread.sleep(1000);
    }

    @Test
    @Order(1)
    public void insertTest() throws InterruptedException {
        ConsensusLog consensusLog = new ConsensusLog(1L, 1L, buildElectionTransaction());
        consensusLogDao.insert(consensusLog).block();
//        StepVerifier.create(consensusLogDao.insert(consensusLog).
//                doOnNext(System.out::println)).
//                expectNextCount(1).
//                verifyComplete();
//        Thread.sleep(1000);
    }

    @Test
    @Order(2)
    public void findByLogIndexTest() throws InterruptedException {
        StepVerifier.create(consensusLogDao.findByLogIndex(1L).
                doOnNext(System.out::println)).
                expectNextCount(1).
                verifyComplete();
        Thread.sleep(1000);
    }

    @Test
    @Order(3)
    public void getTest() throws InterruptedException {

        Consumer<ConsensusLog> onSuccess = (ConsensusLog entry) -> {
            log.info("ConsensusLog.getTest() in onSuccess: "+entry);
            try {
                Thread.sleep(3000);
            } catch(InterruptedException ie) {
                ie.printStackTrace();
            }
        };
        Consumer<Throwable> onError = (Throwable ex) -> {
            ex.printStackTrace();
        };

        Runnable onCompletion = () -> {
            log.info("consensusLog.getTest(): Message Completed");
            try {
                Thread.sleep(3000);
            } catch(InterruptedException ie) {
                ie.printStackTrace();
            }
        };

        Mono.just(
                consensusLogDao.get(1L).
                doOnSuccess(System.out::println).
                doOnError(System.out::println).
                subscribe(onSuccess, onError, onCompletion));
//                block());
    }

    @Test
    @Order(4)
    public void findAllTest() throws InterruptedException {
        StepVerifier.create(consensusLogDao.all().
                doOnNext(System.out::println)).
                expectNextCount(1).
                expectComplete();
//        Thread.sleep(1000);
    }

    @Test
    public void findMaxTest() {

        ConsensusLog consensusLog1 = new ConsensusLog(10L, 11L, buildElectionTransaction());
        ConsensusLog consensusLog2 = new ConsensusLog(100L, 110L, buildElectionTransaction());
        ConsensusLog consensusLog3 = new ConsensusLog(1000L, 1100L, buildElectionTransaction());
        ConsensusLog consensusLog4 = new ConsensusLog(0L, 21L, buildElectionTransaction());
        ConsensusLog consensusLog5 = new ConsensusLog(245L, 110L, buildElectionTransaction());
        ConsensusLog consensusLog6 = new ConsensusLog(959L, 1100L, buildElectionTransaction());
        ConsensusLog consensusLog7 = new ConsensusLog(1L, 1L, buildElectionTransaction());
//
        consensusLogDao.create(consensusLog1).block();
        consensusLogDao.create(consensusLog2).block();
        consensusLogDao.create(consensusLog3).block();
        consensusLogDao.create(consensusLog4).block();
        consensusLogDao.create(consensusLog5).block();
        consensusLogDao.create(consensusLog6).block();
        consensusLogDao.create(consensusLog7).block();
        consensusLogDao.findMaxIndex().
        doOnNext(System.out::println).block();
        consensusLogDao.delete(consensusLog1).block();
        consensusLogDao.delete(consensusLog2).block();
        consensusLogDao.delete(consensusLog3).block();
        consensusLogDao.delete(consensusLog4).block();
        consensusLogDao.delete(consensusLog5).block();
        consensusLogDao.delete(consensusLog6).block();
        consensusLogDao.delete(consensusLog7).block();
    }

    private byte[] buildElectionTransaction() {

        VotingDistrict votingDistrict = new VotingDistrict(1,1,1,"",1,1,1);
        Voter voter = new Voter(2L, UUID.randomUUID(), votingDistrict);
        Map<String, String> office = new HashMap<>();
        office.put("president", "Donald Trump");
        Map<String, String> question = new HashMap<>();
        question.put("article1", "Yes");

        Ballot ballot = new Ballot(UUID.randomUUID(), office, question);

        Election election = new Election(new Date(), "Georgia");

        ElectionTransaction electionTransaction = new ElectionTransaction(voter, election, ballot);
        byte[] et = SerializationUtils.serialize(electionTransaction);

        return et;
    }
}
