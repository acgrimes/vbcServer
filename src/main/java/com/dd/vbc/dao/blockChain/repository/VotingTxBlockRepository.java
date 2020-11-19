package com.dd.vbc.dao.blockChain.repository;

import com.dd.vbc.domain.VotingTxBlock;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public interface VotingTxBlockRepository extends ReactiveMongoRepository<VotingTxBlock, Long> {}
