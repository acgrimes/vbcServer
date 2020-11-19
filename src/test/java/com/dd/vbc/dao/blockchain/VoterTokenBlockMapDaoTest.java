package com.dd.vbc.dao.blockchain;

import com.dd.vbc.dao.blockChain.VoterTokenBlockMapDao;
import com.dd.vbc.db.mongo.MongoConfig;
import com.dd.vbc.domain.VoterTokenBlockMap;
import com.mongodb.client.result.DeleteResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
// ApplicationContext will be loaded from AppConfig and VoterTokenBlockMapDao
@ContextConfiguration(classes = {MongoConfig.class, VoterTokenBlockMapDao.class})
public class VoterTokenBlockMapDaoTest {

    private static final Logger log = LoggerFactory.getLogger(VoterTokenBlockMapDaoTest.class);
    private UUID token = UUID.randomUUID();
    private VoterTokenBlockMap voterTokenBlockMap = new VoterTokenBlockMap(token, 100L);

    @Autowired
    private VoterTokenBlockMapDao voterTokenBlockMapDao;

    @Test
    public void voterTokenBlockMapInsertFindDeleteById() {

        StepVerifier.create(voterTokenBlockMapDao.insert(voterTokenBlockMap).
                doOnNext(System.out::println)).
                expectNextCount(1).
                verifyComplete();

        StepVerifier.create(voterTokenBlockMapDao.findById(token).
                doOnNext(System.out::println)).
                expectNextCount(1).
                verifyComplete();

        StepVerifier.create(voterTokenBlockMapDao.existsById(token).
                doOnNext(System.out::println)).
                expectNextCount(1).
                verifyComplete();

        StepVerifier.create(voterTokenBlockMapDao.deleteById(token).
                doOnNext(System.out::println)).
                expectNextCount(0).
                verifyComplete();

    }

    @Test
    public void setVoterTokenBlockMapSaveDelete() {

        StepVerifier.create(voterTokenBlockMapDao.save(voterTokenBlockMap).
                doOnNext(System.out::println)).
                expectNextCount(1).
                verifyComplete();

        StepVerifier.create(voterTokenBlockMapDao.delete(voterTokenBlockMap).
                doOnNext(System.out::println)).
                expectNextCount(1).
                verifyComplete();
    }

    @Test
    public void voterTokenBlockMapSaveDeleteTest() {

        VoterTokenBlockMap voterTokenBlockMapResult = voterTokenBlockMapDao.save(voterTokenBlockMap).block();
        log.debug(voterTokenBlockMapResult.toString());

        DeleteResult deleteResult = voterTokenBlockMapDao.delete(voterTokenBlockMap).block();
        log.debug(String.valueOf(deleteResult.getDeletedCount()));

    }
}
