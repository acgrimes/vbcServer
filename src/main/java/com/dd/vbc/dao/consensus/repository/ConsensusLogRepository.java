package com.dd.vbc.dao.consensus.repository;

import com.dd.vbc.domain.ConsensusLog;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsensusLogRepository extends ReactiveMongoRepository<ConsensusLog, Long> {
}





