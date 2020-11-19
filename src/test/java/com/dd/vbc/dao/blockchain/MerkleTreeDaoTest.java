package com.dd.vbc.dao.blockchain;

import com.dd.vbc.dao.blockChain.MerkleTreeDao;
import com.dd.vbc.db.mongo.MongoConfig;
import com.dd.vbc.domain.MerkleTree;
import com.dd.vbc.utils.BuildElectionTransaction;
import com.dd.vbc.utils.BuildMerkleTree;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {MongoConfig.class, MerkleTreeDao.class})
public class MerkleTreeDaoTest {

    private static Logger log = LoggerFactory.getLogger(MerkleTreeDaoTest.class);

    private byte[] hash = SerializationUtils.serialize(BuildElectionTransaction.build());
    private MerkleTree merkleTree = BuildMerkleTree.build(21L,1L, hash);

    @Autowired
    private MerkleTreeDao merkleTreeDao;

    @Test
    public void merkleTreeInsertFindDeleteTest() {

        StepVerifier.create(merkleTreeDao.save(merkleTree).
            doOnNext(System.out::println)).
            expectNextCount(1).
            verifyComplete();

        StepVerifier.create(merkleTreeDao.findByBlockId(21L).
            doOnNext(System.out::println)).
            expectNextCount(1).
            verifyComplete();

        StepVerifier.create(merkleTreeDao.existsById(21L).
                doOnNext(System.out::println)).
                expectNextCount(1).
                verifyComplete();

        StepVerifier.create(merkleTreeDao.deleteById(21L).
            doOnNext(System.out::println)).
            expectNextCount(0).
            verifyComplete();
        }

    @Test
    public void merkleTreeSaveDeleteTest() {

        StepVerifier.create(merkleTreeDao.save(merkleTree).
                doOnNext(System.out::println)).
                expectNextCount(1).
                verifyComplete();

        StepVerifier.create(merkleTreeDao.delete(merkleTree).
                doOnNext(System.out::println)).
                expectNextCount(0).
                verifyComplete();

    }

    @Test
    public void merkleTreeDaoUpsertWithInsertUpdateTest() {

        StepVerifier.create(merkleTreeDao.save(merkleTree).
                doOnNext(System.out::println)).
                expectNextCount(1).
                verifyComplete();

        StepVerifier.create(merkleTreeDao.findByBlockId(21L)).
                assertNext(mt -> assertEquals(1, mt.getNodeMap().size())).
                verifyComplete();

        byte[] hash = SerializationUtils.serialize(BuildElectionTransaction.build());
        MerkleTree updatedMerkleTree = BuildMerkleTree.build(21L,2L, hash);

        StepVerifier.create(merkleTreeDao.upsert(updatedMerkleTree).
                doOnNext(System.out::println)).
                expectNextCount(1).
                verifyComplete();

        StepVerifier.create(merkleTreeDao.findByBlockId(21L).
                doOnNext(System.out::println)).
                assertNext(mt -> assertEquals(2, mt.getNodeMap().size())).
                verifyComplete();

        StepVerifier.create(merkleTreeDao.delete(updatedMerkleTree).
                doOnNext(System.out::println)).
                expectNextCount(0).
                verifyComplete();
    }

    @Test
    public void merkleTreeDaoInsertWithUpsertTest() {

        StepVerifier.create(merkleTreeDao.upsert(merkleTree).
                doOnNext(System.out::println)).
                expectNextCount(1).
                verifyComplete();

        StepVerifier.create(merkleTreeDao.findByBlockId(21L).
                doOnNext(System.out::println)).
                expectNextCount(1).
                verifyComplete();

        StepVerifier.create(merkleTreeDao.delete(merkleTree).
                doOnNext(System.out::println)).
                expectNextCount(0).
                verifyComplete();

    }

    @Test
    public void merkleTreeDaoUpdateTest() {

        StepVerifier.create(merkleTreeDao.save(merkleTree).
                doOnNext(System.out::println)).
                expectNextCount(1).
                verifyComplete();

        StepVerifier.create(merkleTreeDao.findByBlockId(21L)).
                assertNext(mt -> assertEquals(1, mt.getNodeMap().size())).
                verifyComplete();

        byte[] hash = SerializationUtils.serialize(BuildElectionTransaction.build());
        MerkleTree updatedMerkleTree = BuildMerkleTree.build(21L,2L, hash);

        StepVerifier.create(merkleTreeDao.update(updatedMerkleTree).
                doOnNext(System.out::println)).
                expectNextCount(1).
                verifyComplete();

        MerkleTree merkleTree = merkleTreeDao.update(updatedMerkleTree).block();
        log.debug(merkleTree.toString());

        StepVerifier.create(merkleTreeDao.findByBlockId(21L).
                doOnNext(System.out::println)).
                assertNext(mt -> assertEquals(2, mt.getNodeMap().size())).
                verifyComplete();

        StepVerifier.create(merkleTreeDao.delete(updatedMerkleTree).
                doOnNext(System.out::println)).
                expectNextCount(0).
                verifyComplete();

    }

    @Test
    public void saveWithBlockTest() {

        merkleTreeDao.save(merkleTree).block();

    }
}
