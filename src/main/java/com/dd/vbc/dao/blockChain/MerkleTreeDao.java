package com.dd.vbc.dao.blockChain;

import com.dd.vbc.dao.blockChain.repository.MerkleTreeRepository;
import com.dd.vbc.domain.MerkleTree;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
public class MerkleTreeDao {

    private static final Logger log = LoggerFactory.getLogger(MerkleTreeDao.class);

    private MerkleTreeRepository merkleTreeRepository;

    @Autowired
    public void setMerkleTreeRepository(MerkleTreeRepository merkleTreeRepository) {
        this.merkleTreeRepository = merkleTreeRepository;
    }

    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    public void setReactiveMongoTemplate(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    public Mono<MerkleTree> save(final MerkleTree merkleTree) {

        return merkleTreeRepository.save(merkleTree).
                doOnSuccess(success -> log.debug("MerkleTree Saved")).
                doOnError(Throwable::printStackTrace);
    }

    public Mono<MerkleTree> insert(final MerkleTree merkleTree) {
        return this.merkleTreeRepository.insert(merkleTree);
    }

    public Mono<MerkleTree> findByBlockId(final Long id) {

        return merkleTreeRepository.findById(id);
    }

    /**
     * method used to remove a merkletree that is complete
     * @param id - block id in the blockchain
     */
    public Mono<Void> deleteById(Long id) {
        return merkleTreeRepository.deleteById(id);
    }


    public Mono<Boolean> existsById(Long id) {
        return merkleTreeRepository.existsById(id);
    }

    /**
     *
     * @param merkleTree
     * @return
     */
    public Mono<MerkleTree> update(MerkleTree merkleTree) {

//        Query query = new Query();
//        query.addCriteria(Criteria.where("blockId").is(merkleTree.getBlockId()));
//
//        return reactiveMongoTemplate.findOne(query, MerkleTree.class).
//            doOnNext(mt -> mt.setMerkleRoot(merkleTree.getMerkleRoot())).
//            doOnNext(mt -> reactiveMongoTemplate.save(mt));

        return reactiveMongoTemplate.update(MerkleTree.class)
                .inCollection("merkleTree")
                .matching(query(where("blockId").is(merkleTree.getBlockId())))
                .replaceWith(merkleTree)
                .findAndReplace();
    }

    /**
     *
     * @param merkleTree
     * @return
     */
    public Mono<Void> delete(MerkleTree merkleTree) {
        return merkleTreeRepository.delete(merkleTree)
                .doOnSuccess(success -> log.debug("MerkleTree Deleted"))
                .doOnError(Throwable::printStackTrace);

    }

    public Mono<UpdateResult> upsert(MerkleTree merkleTree) {

        Query query = new Query();
        query.addCriteria(Criteria.where("blockId").is(merkleTree.getBlockId()));

        Update update = new Update();
        update.set("txCount", merkleTree.getTxCount()).
                set("merkleRoot", merkleTree.getMerkleRoot()).
                set("nodeMap", merkleTree.getNodeMap());

        return reactiveMongoTemplate.upsert(query, update, MerkleTree.class);
    }
}
