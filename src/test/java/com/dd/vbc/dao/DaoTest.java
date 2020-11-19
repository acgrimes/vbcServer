package com.dd.vbc.dao;

import com.dd.vbc.business.services.server.blockchain.BlockChainService;
import com.dd.vbc.dao.blockChain.MerkleTreeDao;
import com.dd.vbc.dao.blockChain.VoterTokenBlockMapDao;
import com.dd.vbc.dao.blockChain.VotingTxBlockDao;
import com.dd.vbc.db.mongo.MongoConfig;
import com.dd.vbc.domain.MerkleTree;
import com.dd.vbc.domain.VoterTokenBlockMap;
import com.dd.vbc.domain.VotingTxBlock;
import com.dd.vbc.utils.BuildElectionTransaction;
import com.dd.vbc.utils.BuildMerkleTree;
import com.dd.vbc.utils.BuildVotingTxBlock;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.UUID;

@SpringBootTest(classes = {BlockChainService.class,
                            MerkleTreeDao.class,
                            VotingTxBlockDao.class,
                            VoterTokenBlockMapDao.class,
                            MongoConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DaoTest {

    private final static Logger log = LoggerFactory.getLogger(DaoTest.class);

    @Autowired
    private MerkleTreeDao merkleTreeDao;

    @Autowired
    private VoterTokenBlockMapDao voterTokenBlockMapDao;

    @Autowired
    private VotingTxBlockDao votingTxBlockDao;

    @Test
    public void allBlockchainDaosTest() {

        byte[] currentHash = SerializationUtils.serialize(BuildElectionTransaction.build());

        MerkleTree merkleTree = BuildMerkleTree.build(21L, 1L, currentHash);
        MerkleTree merkleTreeResult = merkleTreeDaoTest(merkleTree).block();
        log.debug(merkleTreeResult.toString());

        VotingTxBlock votingTxBlock = BuildVotingTxBlock.build(21L, 1l, currentHash);
        VotingTxBlock votingTxBlockResult = votingTxBlockDao.insert(votingTxBlock).block();
        log.debug(votingTxBlockResult.toString());

        VoterTokenBlockMap voterTokenBlockMap = new VoterTokenBlockMap(UUID.randomUUID(), 21L);
        VoterTokenBlockMap voterTokenBlockMapResult = voterTokenBlockMapDao.save(voterTokenBlockMap).block();
        log.debug(voterTokenBlockMapResult.toString());

    }

    private Mono<MerkleTree> merkleTreeDaoTest(MerkleTree merkleTree) {

        merkleTreeDao.save(merkleTree).block();
        return merkleTreeDao.findByBlockId(merkleTree.getBlockId());

    }

    private Mono<VoterTokenBlockMap> voterTokenBlockMapTest(VoterTokenBlockMap voterTokenBlockMap) {

        voterTokenBlockMapDao.save(voterTokenBlockMap);
        return voterTokenBlockMapDao.findById(voterTokenBlockMap.getToken());

    }

    private Mono<VotingTxBlock> votingTxBlockTest(VotingTxBlock votingTxBlock) {

        votingTxBlockDao.insert(votingTxBlock);
        return votingTxBlockDao.findByBlockId(votingTxBlock.getBlockId());

    }
}
