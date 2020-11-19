package com.dd.vbc.dao.blockchain;

import com.dd.vbc.dao.blockChain.VotingTxBlockDao;
import com.dd.vbc.db.mongo.MongoConfig;
import com.dd.vbc.domain.VotingTxBlock;
import com.dd.vbc.utils.BinaryHexConverter;
import com.dd.vbc.utils.BuildElectionTransaction;
import com.dd.vbc.utils.BuildVotingTxBlock;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {MongoConfig.class, VotingTxBlockDao.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VotingTxBlockDaoTest {

    @Autowired
    private VotingTxBlockDao votingTxBlockDao;


    @Test
    public void votingTxBlockInsertFindUpdateDelete() {
        byte[] hash = SerializationUtils.serialize(BuildElectionTransaction.build());
        String hashStr = BinaryHexConverter.bytesToHex(hash);
        VotingTxBlock votingTxBlock = BuildVotingTxBlock.build(10L, 1L, hash);

        byte[] hashReplace = SerializationUtils.serialize(hash);
        String hashReplaceStr = BinaryHexConverter.bytesToHex(hashReplace);
        VotingTxBlock votingTxBlockReplace = BuildVotingTxBlock.build(10L, 20L, hashReplace);


        StepVerifier.create(votingTxBlockDao.insert(votingTxBlock).
                doOnNext(vtb -> System.out.println("Insert BlockId = "+vtb.getBlockId()))).
                assertNext(vtb -> assertEquals(10L, vtb.getBlockId())).
                verifyComplete();

        VotingTxBlock vtbInsert = votingTxBlockDao.findByBlockId(10L).block();
        assertEquals(hashStr, BinaryHexConverter.bytesToHex(vtbInsert.getVotingBlockHeader().getMerkleRoot()));

        votingTxBlockDao.update(votingTxBlockReplace).block();
        VotingTxBlock vtbUpdate = votingTxBlockDao.findByBlockId(10L).block();
        assertEquals(hashReplaceStr, BinaryHexConverter.bytesToHex(vtbUpdate.getVotingBlockHeader().getMerkleRoot()));

        StepVerifier.create(votingTxBlockDao.delete(votingTxBlockReplace)).
                expectNextCount(0).
                verifyComplete();

    }

    @Test
    public void votingTxBlockDaoNullCreateTest() {

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> votingTxBlockDao.create(null));
        assertEquals("Entity must not be null!", exception.getMessage());
        assertTrue(exception.getMessage().contains("null"));

    }

    @Test
    public void setVotingTxBlockDaoEmptyCreateTest() {

        VotingTxBlock votingTxBlock = new VotingTxBlock();
        StepVerifier.create(votingTxBlockDao.create(votingTxBlock)).
                expectError(InvalidDataAccessApiUsageException.class).
                verify();

    }
}
