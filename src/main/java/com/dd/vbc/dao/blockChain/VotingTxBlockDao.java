package com.dd.vbc.dao.blockChain;

import com.dd.vbc.dao.blockChain.repository.VotingTxBlockRepository;
import com.dd.vbc.domain.VotingTxBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
public class VotingTxBlockDao {

    private static final Logger log = LoggerFactory.getLogger(VotingTxBlockDao.class);

    private VotingTxBlockRepository votingTxBlockRepository;

    @Autowired
    public void setVotingTxBlockRepository(VotingTxBlockRepository votingTxBlockRepository) {
        this.votingTxBlockRepository = votingTxBlockRepository;
    }

    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    public void setReactiveMongoTemplate(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    public Mono<Void> delete(final VotingTxBlock votingTxBlock) {
        return votingTxBlockRepository.delete(votingTxBlock)
                .doOnSuccess(success -> log.debug("DELETE: "+votingTxBlock.toString()))
                .doOnError(Throwable::printStackTrace);
    }

    public Mono<VotingTxBlock> create(final VotingTxBlock votingTxBlock) {
        return votingTxBlockRepository.save(votingTxBlock)
                .doOnSuccess(success -> log.debug("create SAVED: "+votingTxBlock.toString()))
                .doOnError(Throwable::printStackTrace);
    }

    public Mono<VotingTxBlock> insert(final VotingTxBlock votingTxBlock) {
        return votingTxBlockRepository.save(votingTxBlock)
                .doOnSuccess(success -> log.debug("insert SAVED: "+votingTxBlock.toString()))
                .doOnError(Throwable::printStackTrace);
    }

    public Mono<VotingTxBlock> findByBlockId(final Long blockId) {
        return votingTxBlockRepository.findById(blockId);
    }

    public Mono<VotingTxBlock> findMaxBlockId() {
        return votingTxBlockRepository.
                findAll(Sort.by(Sort.Direction.DESC, "blockId")).next();
    }

    public Mono<VotingTxBlock> update(final VotingTxBlock votingTxBlock) {

        return reactiveMongoTemplate.update(VotingTxBlock.class)
                .inCollection("votingTxBlock")
                .matching(query(where("blockId").is(votingTxBlock.getBlockId())))
                .replaceWith(votingTxBlock)
                .findAndReplace()
                .doOnSuccess(success -> log.debug("UPDATE: "+votingTxBlock.toString()))
                .doOnError(Throwable::printStackTrace);

    }
}
