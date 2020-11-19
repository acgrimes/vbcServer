package com.dd.vbc.business.services.server.blockchain;

import com.dd.vbc.dao.blockChain.MerkleTreeDao;
import com.dd.vbc.dao.blockChain.VoterTokenBlockMapDao;
import com.dd.vbc.dao.blockChain.VotingTxBlockDao;
import com.dd.vbc.db.mongo.MongoConfig;
import com.dd.vbc.domain.*;
import com.dd.vbc.messageService.request.ElectionRequest;
import com.dd.vbc.utils.*;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {BlockChainService.class,
                            MerkleTreeDao.class,
                            VotingTxBlockDao.class,
                            VoterTokenBlockMapDao.class,
                            MongoConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BlockChainServiceMockTest {

    private static final Logger log = LoggerFactory.getLogger(BlockChainServiceMockTest.class);

    @SpyBean
    private BlockChainService blockChainService;

    @MockBean
    private VotingTxBlockDao votingTxBlockDao;

    @MockBean
    private MerkleTreeDao merkleTreeDao;

    @MockBean
    private VoterTokenBlockMapDao voterTokenBlockMapDao;

    @BeforeAll
    public void setupStaticVariables() {

        ConsensusState.initializeServerList();
        ConsensusState.initializeProxyServer();

        ConsensusState.getServerList().stream().
                forEach(serv -> {
                    if(serv.getId().equals("A")) {
                        ConsensusServer.setId(serv.getId());
                        ConsensusServer.setHost(serv.getHost());
                        ConsensusServer.setHttpPort(serv.getHttpPort());
                        ConsensusServer.setReactivePort(serv.getReactivePort());
                        ConsensusServer.setState(serv.getState());
                    }
                });
        log.debug("Server Id is: "+ConsensusServer.getId());

        //TODO: these values need to be read from database.
        ConsensusState.setCurrentIndex(new AtomicLong(1L));
        ConsensusState.setCurrentTerm(new AtomicLong(1L));

        //TODO: these values need to be read from database:
        BlockChainMetadata.setActiveBlock(new AtomicLong(1L));
        BlockChainMetadata.setActiveBlockTxCount(new AtomicLong(0L));

    }

    @Test
    public void generateMerkleTreeTest() {

        try {
            byte[] currentHash = SerializationUtils.serialize(BuildElectionTransaction.build());
            String currentHashStr = BinaryHexConverter.bytesToHex(currentHash);
            ElectionRequest electionRequest = BuildElectionRequest.build();
            blockChainService.setvToken(electionRequest.getVoter().getVtoken());
            BlockChainMetadata.setBlockChainHash(currentHash);
            BlockChainMetadata.setActiveBlock(new AtomicLong(21L));
            BlockChainMetadata.setActiveBlockTxCount(new AtomicLong(7L));
            MerkleTree merkleTree = BuildMerkleTree.build(BlockChainMetadata.getActiveBlock().get(),
                                                          BlockChainMetadata.getActiveBlockTxCount().get(),
                                                          currentHash);

            AppendEntry appendEntry = new AppendEntry(ConsensusServer.getServerInstance(),
                    BlockChainMetadata.getActiveBlock().get(),
                    BlockChainMetadata.getActiveBlockTxCount().get(),
                    electionRequest.getElectionTransaction());

            Mockito.when(merkleTreeDao.findByBlockId(BlockChainMetadata.getActiveBlock().get())).thenReturn(Mono.just(merkleTree));
            Mockito.when(merkleTreeDao.upsert(merkleTree)).thenReturn(null);
            MerkleTree aMerkleTree = blockChainService.generateMerkleTree(appendEntry).block();

            assertEquals(21l, aMerkleTree.getBlockId(), "after update BlockId should be 21");
            assertEquals(8L, aMerkleTree.getTxCount(), "after update Tx Count should be 8");
            assertEquals(8L, aMerkleTree.getNodeMap().entrySet().size(), "after update merkleTree nodes should be 8");

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void generateMerkleTreeFindByBlockIdNullTest() {

        try {
            byte[] currentHash = SerializationUtils.serialize(BuildElectionTransaction.build());
            String currentHashStr = BinaryHexConverter.bytesToHex(currentHash);
            ElectionRequest electionRequest = BuildElectionRequest.build();
            blockChainService.setvToken(electionRequest.getVoter().getVtoken());
            BlockChainMetadata.setBlockChainHash(currentHash);
            BlockChainMetadata.setActiveBlock(new AtomicLong(22L));
            BlockChainMetadata.setActiveBlockTxCount(new AtomicLong(0L));
            MerkleTree merkleTree = BuildMerkleTree.build(BlockChainMetadata.getActiveBlock().get(),
                    BlockChainMetadata.getActiveBlockTxCount().get(),
                    currentHash);

            AppendEntry appendEntry = new AppendEntry(ConsensusServer.getServerInstance(),
                    BlockChainMetadata.getActiveBlock().get(),
                    BlockChainMetadata.getActiveBlockTxCount().get(),
                    electionRequest.getElectionTransaction());

            Mockito.when(merkleTreeDao.findByBlockId(BlockChainMetadata.getActiveBlock().get())).thenReturn(null);
            Mockito.when(merkleTreeDao.upsert(merkleTree)).thenReturn(null);
            MerkleTree aMerkleTree = blockChainService.generateMerkleTree(appendEntry).block();

            assertEquals(22l, aMerkleTree.getBlockId(), "after update BlockId should be 21");
            assertEquals(1L, aMerkleTree.getTxCount(), "after update Tx Count should be 1");
            assertEquals(1L, aMerkleTree.getNodeMap().entrySet().size(), "after update merkleTree nodes should be 1");

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void updateTxBlockTest() {

        byte[] currentHash = SerializationUtils.serialize(BuildElectionTransaction.build());
        String currentHashStr = BinaryHexConverter.bytesToHex(currentHash);
        ElectionRequest electionRequest = BuildElectionRequest.build();
        blockChainService.setvToken(electionRequest.getVoter().getVtoken());
        BlockChainMetadata.setBlockChainHash(currentHash);
        BlockChainMetadata.setActiveBlock(new AtomicLong(21L));
        BlockChainMetadata.setActiveBlockTxCount(new AtomicLong(1L));

        VotingTxBlock votingTxBlock = BuildVotingTxBlock.build(BlockChainMetadata.getActiveBlock().get(),
                                                      BlockChainMetadata.getActiveBlockTxCount().get()-1L,
                                                               currentHash);

        Mockito.when(votingTxBlockDao.insert(votingTxBlock)).thenReturn(Mono.just(votingTxBlock));
        votingTxBlock.getVotingBlockHeader().setTxCount(BlockChainMetadata.getActiveBlockTxCount().get());
        Mockito.when(votingTxBlockDao.update(votingTxBlock)).thenReturn(Mono.just(votingTxBlock));

        VotingTxBlock votingTxBlockResult = blockChainService.updateTxBlock(votingTxBlock, electionRequest.getElectionTransaction()).block();

        Assert.assertEquals(votingTxBlock, votingTxBlockResult);

    }

//    @Test
    protected void createNewTxBlockTest() {

        try {
            ElectionRequest electionRequest = BuildElectionRequest.build();
            blockChainService.setvToken(electionRequest.getVoter().getVtoken());
            byte[] hash = electionRequest.getElectionTransaction();
            BlockChainMetadata.setBlockChainHash(hash);
            String hashStr = BinaryHexConverter.bytesToHex(hash);

//            Mockito.doReturn(Mono.just(votingTxBlock)).when(votingTxBlockDao).findByBlockId(BlockChainMetadata.getActiveBlock().get());

            VotingTxBlock votingTxBlockResult = blockChainService.createNewTxBlock(hash).block();

            VotingTxBlock vtb = votingTxBlockDao.findByBlockId(BlockChainMetadata.getActiveBlock().get()).block();
//            assertEquals(votingTxBlock.getBlockId(), vtb.getBlockId());
            assertEquals(1L, vtb.getVotingBlockHeader().getTxCount(), "vtb.getBlockId() = " + vtb.getVotingBlockHeader().getTxCount());
            String vtbHashStr = BinaryHexConverter.bytesToHex(vtb.getVotingBlockHeader().getPreviousBlockHash());
            assertEquals(hashStr, vtbHashStr);

            votingTxBlockDao.delete(vtb).block();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test
    protected void createVoterTokenBlockMapTest() {

        byte[] currentHash = SerializationUtils.serialize(BuildElectionTransaction.build());
        ElectionRequest electionRequest = BuildElectionRequest.build();
        blockChainService.setvToken(electionRequest.getVoter().getVtoken());
        BlockChainMetadata.setBlockChainHash(currentHash);
        BlockChainMetadata.setActiveBlock(new AtomicLong(21L));
        BlockChainMetadata.setActiveBlockTxCount(new AtomicLong(1L));

        VotingTxBlock votingTxBlock = BuildVotingTxBlock.build(BlockChainMetadata.getActiveBlock().get(),
                                                               BlockChainMetadata.getActiveBlockTxCount().get(),
                                                               currentHash);
        VoterTokenBlockMap map = new VoterTokenBlockMap(electionRequest.getVoter().getVtoken(), votingTxBlock.getBlockId());
        Mockito.when(voterTokenBlockMapDao.save(map)).thenReturn(Mono.just(map));

        VoterTokenBlockMap mapResult = blockChainService.createVoterTokenBlockMap(votingTxBlock).block();

        Assert.assertEquals(map, mapResult);
    }

    @Test
    protected void extendBallotBlockchainCreateNewTxBlockTest() {

        byte[] currentHash = SerializationUtils.serialize(BuildElectionTransaction.build());
        VotingTxBlock votingTxBlock = BuildVotingTxBlock.build(BlockChainMetadata.getActiveBlock().get(),
                                                               BlockChainMetadata.getActiveBlockTxCount().get(),
                                                               currentHash);
        ElectionRequest electionRequest = BuildElectionRequest.build();
        blockChainService.setvToken(electionRequest.getVoter().getVtoken());
        AppendEntry appendEntry = new AppendEntry(ConsensusServer.getServerInstance(),
                BlockChainMetadata.getActiveBlock().get(),
                BlockChainMetadata.getActiveBlockTxCount().get(),
                electionRequest.getElectionTransaction());
        appendEntry.setvToken(electionRequest.getVoter().getVtoken());

        BlockChainMetadata.setActiveBlockTxCount(new AtomicLong(8L));

        VotingTxBlock votingTxBlockResetTxCount = new VotingTxBlock();
        votingTxBlockResetTxCount.setBlockId(BlockChainMetadata.getActiveBlock().get()+1L);
        votingTxBlockResetTxCount.setTransactionMap(votingTxBlock.getTransactionMap());
        votingTxBlockResetTxCount.setVotingBlockHeader(votingTxBlock.getVotingBlockHeader());
        votingTxBlockResetTxCount.getVotingBlockHeader().setTxCount(1L);
        Mockito.doReturn(Mono.just(votingTxBlockResetTxCount)).when(blockChainService).createNewTxBlock(appendEntry.getElectionTransaction());
        Mockito.doReturn(Mono.just(votingTxBlock)).when(votingTxBlockDao).findByBlockId(BlockChainMetadata.getActiveBlock().get());

        VotingTxBlock result = blockChainService.extendBallotBlockchain(appendEntry).block();

        Assert.assertEquals(votingTxBlockResetTxCount, result);
    }

    @Test
    protected void extendBallotBlockchainUpdateTxBlockTest() {

        log.debug("Entering extendBallotBlockchainUpdateTxBlockTest");

        byte[] currentHash = SerializationUtils.serialize(BuildElectionTransaction.build());
        VotingTxBlock votingTxBlock = BuildVotingTxBlock.build(BlockChainMetadata.getActiveBlock().get(),
                BlockChainMetadata.getActiveBlockTxCount().get(),
                currentHash);
        ElectionRequest electionRequest = BuildElectionRequest.build();
        blockChainService.setvToken(electionRequest.getVoter().getVtoken());
        AppendEntry appendEntry = new AppendEntry(ConsensusServer.getServerInstance(),
                BlockChainMetadata.getActiveBlock().get(),
                BlockChainMetadata.getActiveBlockTxCount().get(),
                electionRequest.getElectionTransaction());
        appendEntry.setvToken(electionRequest.getVoter().getVtoken());
        VotingTxBlock votingTxBlockTxCount = new VotingTxBlock();
        votingTxBlockTxCount.setBlockId(BlockChainMetadata.getActiveBlock().get());
        votingTxBlockTxCount.setTransactionMap(votingTxBlock.getTransactionMap());
        votingTxBlockTxCount.setVotingBlockHeader(votingTxBlock.getVotingBlockHeader());
        votingTxBlockTxCount.getVotingBlockHeader().setTxCount(BlockChainMetadata.getActiveBlockTxCount().get()+1L);
        Mockito.doReturn(Mono.just(votingTxBlockTxCount)).when(blockChainService).updateTxBlock(votingTxBlock, electionRequest.getElectionTransaction());
        Mockito.doReturn(Mono.just(votingTxBlock)).when(votingTxBlockDao).findByBlockId(BlockChainMetadata.getActiveBlock().get());

        VotingTxBlock result = blockChainService.extendBallotBlockchain(appendEntry).block();

        Assert.assertEquals(votingTxBlockTxCount, result);
    }

    @Test
    public void followerCommitEntryResponseTrueTest() {

        log.debug("Entering extendBallotBlockchainUpdateTxBlockTest");

        byte[] currentHash = SerializationUtils.serialize(BuildElectionTransaction.build());
        VotingTxBlock votingTxBlock = BuildVotingTxBlock.build(BlockChainMetadata.getActiveBlock().get(),
                BlockChainMetadata.getActiveBlockTxCount().get(),
                currentHash);
        ElectionRequest electionRequest = BuildElectionRequest.build();
        blockChainService.setvToken(electionRequest.getVoter().getVtoken());
        AppendEntry appendEntry = new AppendEntry(ConsensusServer.getServerInstance(),
                BlockChainMetadata.getActiveBlock().get(),
                BlockChainMetadata.getActiveBlockTxCount().get(),
                electionRequest.getElectionTransaction());
        appendEntry.setvToken(electionRequest.getVoter().getVtoken());
        appendEntry.setCommitted(Boolean.FALSE);
        appendEntry.setBlockChainHash(null);

        Mockito.doReturn(Mono.just(appendEntry)).when(blockChainService).voterBlockChainProcess(appendEntry);
        BlockChainMetadata.setBlockChainHash(currentHash);

        AppendEntry appendEntryResult = blockChainService.followerCommitEntryResponse(appendEntry).block();

        Assert.assertEquals(appendEntry, appendEntryResult);
        Assert.assertEquals(Boolean.TRUE, appendEntryResult.getCommitted());
        Assert.assertTrue(appendEntryResult.getBlockChainHash()!=null);
    }

    @Test
    public void followerCommitEntryResponseFalseTest() {

        log.debug("Entering extendBallotBlockchainUpdateTxBlockTest");

        byte[] currentHash = SerializationUtils.serialize(BuildElectionTransaction.build());
        VotingTxBlock votingTxBlock = BuildVotingTxBlock.build(BlockChainMetadata.getActiveBlock().get(),
                BlockChainMetadata.getActiveBlockTxCount().get(),
                currentHash);
        ElectionRequest electionRequest = BuildElectionRequest.build();
        blockChainService.setvToken(electionRequest.getVoter().getVtoken());
        AppendEntry appendEntry = new AppendEntry(ConsensusServer.getServerInstance(),
                BlockChainMetadata.getActiveBlock().get(),
                BlockChainMetadata.getActiveBlockTxCount().get(),
                null);
        appendEntry.setvToken(null);
        appendEntry.setCommitted(Boolean.FALSE);
        appendEntry.setBlockChainHash(null);

        Mockito.doThrow(new NullPointerException("Error occurred")).when(blockChainService).voterBlockChainProcess(appendEntry);
        BlockChainMetadata.setBlockChainHash(currentHash);

        AppendEntry appendEntryResult = blockChainService.followerCommitEntryResponse(appendEntry).block();

        Assert.assertEquals(appendEntry, appendEntryResult);
        Assert.assertEquals(Boolean.FALSE, appendEntryResult.getCommitted());
        Assert.assertTrue(appendEntryResult.getBlockChainHash()==null);
    }
}
