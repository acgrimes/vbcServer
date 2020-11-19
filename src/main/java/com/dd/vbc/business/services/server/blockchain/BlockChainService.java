package com.dd.vbc.business.services.server.blockchain;

import com.dd.vbc.dao.blockChain.MerkleTreeDao;
import com.dd.vbc.dao.blockChain.VoterTokenBlockMapDao;
import com.dd.vbc.dao.blockChain.VotingTxBlockDao;
import com.dd.vbc.domain.*;
import com.dd.vbc.merkle.HashType;
import com.dd.vbc.merkle.Merkle;
import com.dd.vbc.utils.BinaryHexConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.dd.vbc.utils.ByteArrayUtils.concatenatingTwoByteArrays;

@Service
public class BlockChainService {

    private static final Logger log = LoggerFactory.getLogger(BlockChainService.class);

    private UUID vToken;

    UUID getvToken() {
        return vToken;
    }

    void setvToken(UUID vToken) {
        this.vToken = vToken;
    }

    private MerkleTreeDao merkleTreeDao;

    @Autowired
    public void setMerkleTreeDao(MerkleTreeDao merkleTreeDao) {
        this.merkleTreeDao = merkleTreeDao;
    }

    private VotingTxBlockDao votingTxBlockDao;

    @Autowired
    public void setVotingTxBlockDao(VotingTxBlockDao votingTxBlockDao) {
        this.votingTxBlockDao = votingTxBlockDao;
    }

    private VoterTokenBlockMapDao voterTokenBlockMapDao;

    @Autowired
    public void setVoterTokenBlockMapDao(VoterTokenBlockMapDao voterTokenBlockMapDao) {
        this.voterTokenBlockMapDao = voterTokenBlockMapDao;
    }

    /**
     * The Follower response when a Leader requests a commit of an ElectionTransaction.
     * @param leaderAppendEntry - includes a request to commit ElectionTransaction
     * @return - Mono<AppendEntry> including commit=true and blockChain hash
     */
    public Mono<AppendEntry> followerCommitEntryResponse(final AppendEntry leaderAppendEntry) {

        if(log.isDebugEnabled())
            log.debug("Entering followerCommitEntryResponse: "+leaderAppendEntry.toString());

        setvToken(leaderAppendEntry.getvToken());
        leaderAppendEntry.setServer(ConsensusServer.getServerInstance());
//        try {
//            voterBlockChainProcess(leaderAppendEntry).
//                    doOnSuccess(success -> log.debug("followerCommitEntryResponse - doOnSuccess: "+leaderAppendEntry.toString())).
//                    doOnError(Throwable::printStackTrace).
//                    subscribe();
//        } catch(Exception ex) {
//            leaderAppendEntry.setCommitted(false);
//            log.error(ex.getLocalizedMessage());
//        }
//        log.debug("Return from followerCommitEntryResponse: "+leaderAppendEntry.toString());
//        return Mono.just(leaderAppendEntry);

          return voterBlockChainProcess(leaderAppendEntry).
              doOnSuccess(success -> { if(log.isDebugEnabled())
                                            log.debug("followerCommitEntryResponse - doOnSuccess: "+leaderAppendEntry.toString());}).
              doOnError(Throwable::printStackTrace);
    }

    /**
     *
     * @param appendEntry
     * @return
     */
    public Mono<AppendEntry> voterBlockChainProcess(final AppendEntry appendEntry) {
        if(log.isDebugEnabled())
            log.debug("Entering voterBlockChainProcess: "+appendEntry.toString());

        return Mono.just(appendEntry).
            flatMap(this::extendBallotBlockchain).
            flatMap(this::createVoterTokenBlockMap).
            flatMap(vtbm -> generateMerkleTree(appendEntry)).
            flatMap(this::updateTxBlockHeader).
            flatMap(vtb -> {
                appendEntry.setCommitted(true);
                appendEntry.setBlockChainHash(BlockChainMetadata.getBlockChainHash());
                return Mono.just(appendEntry)
                        .doOnSuccess(success -> { if(log.isDebugEnabled())
                                                    log.debug("voterBlockChainProcess - doOnSuccess: "+appendEntry.toString());})
                        .doOnError(Throwable::printStackTrace);
            });
    }

    /**
     *
     * @param appendEntry
     * @return
     */
    protected Mono<VotingTxBlock> extendBallotBlockchain(AppendEntry appendEntry) {

        if(log.isDebugEnabled())
            log.debug("Entering extendBallotBlockchain: "+appendEntry.toString());

        Mono<VotingTxBlock> votingTxBlockM;
        if (BlockChainMetadata.ELECTION_TX_PER_BLOCK >= BlockChainMetadata.getActiveBlockTxCount().incrementAndGet()) {
            votingTxBlockM = updateTxBlock(null, appendEntry.getElectionTransaction());
        } else {
            votingTxBlockM = createNewTxBlock(appendEntry.getElectionTransaction());
        }
        return votingTxBlockM;
    }

    /**
     * Create a VoterTokenBlockMap and saves to database
     * @param votingTxBlock
     * @return Mono<VoterTokenBlockMap>
     */
    protected Mono<VoterTokenBlockMap> createVoterTokenBlockMap(VotingTxBlock votingTxBlock) {

        if(log.isDebugEnabled())
            log.debug("Entering createVoterTokenBlockMap: "+votingTxBlock.toString());

        VoterTokenBlockMap map = new VoterTokenBlockMap(getvToken(), votingTxBlock.getBlockId());
        if(log.isDebugEnabled())
            log.debug(map.toString());
        return voterTokenBlockMapDao.save(map);
    }

    /**
     *
     * @param vtb
     * @param electionTransaction
     * @return
     */
    protected Mono<VotingTxBlock> updateTxBlock(VotingTxBlock vtb, byte[] electionTransaction) {

        if(log.isDebugEnabled()) log.debug("Entering updateTxBlock: ");

        return votingTxBlockDao.findByBlockId(BlockChainMetadata.getActiveBlock().get()).
            flatMap(votingTxBlock -> {
                votingTxBlock.getTransactionMap().put(getvToken(),
                                                      electionTransaction);
                votingTxBlock.getVotingBlockHeader().setTxCount(BlockChainMetadata.getActiveBlockTxCount().get());
                if(log.isDebugEnabled()) log.debug("updateTxBlock: "+votingTxBlock.toString());
                return votingTxBlockDao.update(votingTxBlock);
            }).
            switchIfEmpty(Mono.defer(() -> createNewTxBlock(electionTransaction)));
    }

    /**
     *
     * @param appendEntry
     * @return
     */
    protected Mono<MerkleTree> generateMerkleTree(AppendEntry appendEntry) {

        if(log.isDebugEnabled()) log.debug("Entering generateMerkleTree: "+appendEntry);

        Mono<MerkleTree> merkleTreeMono = merkleTreeDao.findByBlockId(BlockChainMetadata.getActiveBlock().get()).
            flatMap(merkleTree -> {
                MerkleTree merkleTreeResult = updateMerkleTree(merkleTree, appendEntry.getElectionTransaction());
                Mono<MerkleTree> merkleTreeUpdate = merkleTreeDao.update(merkleTreeResult);
                return merkleTreeUpdate;
            }).
            switchIfEmpty(Mono.defer(() -> generateNewMerkleTree(appendEntry)));
        return merkleTreeMono;
    }

    /**
     *
     * @param appendEntry
     * @return
     */
    protected Mono<MerkleTree> generateNewMerkleTree(AppendEntry appendEntry) {

        if(log.isDebugEnabled()) log.debug("Entering generateNewMerkleTree: "+appendEntry.toString());

        MerkleTree merkleTree = new MerkleTree(null, null, null, new HashMap<>());
        merkleTree = updateMerkleTree(merkleTree, appendEntry.getElectionTransaction());
        return merkleTreeDao.save(merkleTree);
    }

    /**
     *
     * @param merkleTree
     * @param electionTransaction
     * @return
     */
    protected MerkleTree updateMerkleTree(MerkleTree merkleTree, byte[] electionTransaction) {

        if(log.isDebugEnabled()) log.debug("Entering updateMerkleTree: "+merkleTree.toString()+", electionTransaction: "+BinaryHexConverter.bytesToHex(electionTransaction));

        merkleTree.getNodeMap().put(getvToken(), electionTransaction);
        Collection<byte[]> nodes = merkleTree.getNodeMap().values();
        Merkle merkleEngine = new Merkle(HashType.DOUBLE_SHA256);
        byte[] tree = merkleEngine.makeTree(new ArrayList<>(nodes));
        merkleTree.setMerkleRoot(tree);
        merkleTree.setBlockId(BlockChainMetadata.getActiveBlock().get());
        merkleTree.setTxCount(BlockChainMetadata.getActiveBlockTxCount().get());
        return merkleTree;
    }

    /**
     *
     * @param merkleTree
     * @return
     */
    protected Mono<VotingTxBlock> updateTxBlockHeader(final MerkleTree merkleTree) {

        if(log.isDebugEnabled()) log.debug("Entering updateTxBlockHeader: "+merkleTree.toString());

        Mono<VotingTxBlock> votingTxBlockMono = votingTxBlockDao.findByBlockId(BlockChainMetadata.getActiveBlock().get()).
            flatMap(vtb -> {
                vtb.getVotingBlockHeader().setMerkleRoot(merkleTree.getMerkleRoot());
                blockChainHash(vtb);
                return votingTxBlockDao.update(vtb);
            });
        return votingTxBlockMono;
    }

    /**
     * 
     * @param electionTransaction
     * @return - Mono<VotingTxBlock>
     */
    protected Mono<VotingTxBlock> createNewTxBlock(final byte[] electionTransaction) {

        if(log.isDebugEnabled()) log.debug("Entering createNewTxBlock: "+ BinaryHexConverter.bytesToHex(electionTransaction));

        VotingTxBlock votingTxBlock = new VotingTxBlock();
        Version version = new Version(1, 2);
        VotingBlockHeader votingBlockHeader = new VotingBlockHeader();
        BlockChainMetadata.setActiveBlockTxCount(new AtomicLong(1L));
        votingTxBlock.getTransactionMap().put(vToken,
                                              electionTransaction);
        votingBlockHeader.setTxCount(BlockChainMetadata.getActiveBlockTxCount().get());
        votingBlockHeader.setPreviousBlockHash(BlockChainMetadata.getBlockChainHash());
        votingBlockHeader.setVersion(version);
        votingBlockHeader.setDateTime(new Date());
        votingTxBlock.setVotingBlockHeader(votingBlockHeader);
        votingTxBlock.setBlockId(BlockChainMetadata.getActiveBlock().incrementAndGet());
        if(log.isDebugEnabled()) log.debug("createNewTxBlock method: "+votingTxBlock.toString());
        return votingTxBlockDao.insert(votingTxBlock);
    }

    /**
     * This method generates the blockchain hash. This hash is created from the top level block parent hash and
     * merkle root. The two byte[]s are concatenated and that byte[] is hashed.
     * @param votingTxBlock
     */
    protected void blockChainHash(final VotingTxBlock votingTxBlock) {

        if(log.isDebugEnabled()) log.debug("Entering blockChainHash: "+votingTxBlock.toString());

        if(votingTxBlock.getVotingBlockHeader().getPreviousBlockHash()==null) {
            votingTxBlock.getVotingBlockHeader().setPreviousBlockHash(votingTxBlock.getVotingBlockHeader().getMerkleRoot());
        }
        byte[] blockchain = concatenatingTwoByteArrays(votingTxBlock.getVotingBlockHeader().getMerkleRoot(),
                                                       votingTxBlock.getVotingBlockHeader().getPreviousBlockHash());
        try {
            Security.addProvider(new BouncyCastleProvider());
            MessageDigest hash = MessageDigest.getInstance("SHA512", "BC");
            byte[] blockchainHash = hash.digest(blockchain);
            BlockChainMetadata.setBlockChainHash(blockchainHash);
        } catch(NoSuchAlgorithmException | NoSuchProviderException ex) {
            ex.printStackTrace();
        }
    }
}
