package com.dd.vbc.dao.consensus;

import com.dd.vbc.dao.consensus.repository.ConsensusLogRepository;
import com.dd.vbc.domain.AppendEntry;
import com.dd.vbc.domain.ConsensusLog;
import com.mongodb.client.result.DeleteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
public class ConsensusLogDao {

    private static final Logger log = LoggerFactory.getLogger(ConsensusLog.class);

    private ReactiveMongoTemplate reactiveMongoTemplate;
    private ConsensusLogRepository consensusLogRepository;

    @Autowired
    public void setConsensusLogRepository(ConsensusLogRepository consensusLogRepository) {
        this.consensusLogRepository = consensusLogRepository;
    }

    @Autowired
    public void setReactiveMongoTemplate(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    public Flux<ConsensusLog> all() {
        return this.consensusLogRepository.findAll();
    }

    public Mono<ConsensusLog> get(Long logIndex) {
        return this.consensusLogRepository.findById(logIndex);
    }

//    public Mono<UpdateResult> update(ConsensusLog consensusLog) {
//        return this.reactiveMongoTemplate
//                .update(ConsensusLog.class)
//                .apply(query(where("logIndex").is("")));
//    }

    public Mono<DeleteResult> delete(ConsensusLog consensusLog) {
        return this.reactiveMongoTemplate
                .remove(consensusLog, "consensusLog");
    }

    public Mono<ConsensusLog> create(ConsensusLog consensusLog) {
        return consensusLogRepository.save(consensusLog);
    }

    public Mono<ConsensusLog> create(Long index, Long term, byte[] electionTransaction) {
        ConsensusLog consensusLog = new ConsensusLog(index, term, electionTransaction);
        return this.consensusLogRepository
                .save(consensusLog)
                .doOnSuccess(success -> log.debug("CREATE: index "+consensusLog.toString()))
                .doOnError(Throwable::printStackTrace);
    }

    public Mono<ConsensusLog> create(AppendEntry appendEntry) {
        return create(appendEntry.getIndex(), appendEntry.getTerm(), appendEntry.getElectionTransaction());
    }

    /**
     *
     * @param consensusLog - log entry
     * @return - Mono<ConsensusLog>
     */
    public Mono<ConsensusLog> save(ConsensusLog consensusLog) {
        return this.consensusLogRepository
                .save(consensusLog)
                .doOnSuccess(success -> log.debug("save: "+consensusLog.toString()))
                .doOnError(Throwable::printStackTrace);
    }

    public Mono<ConsensusLog> insert(ConsensusLog consensusLog) {
        return this.consensusLogRepository.insert(consensusLog);
    }

    public Mono<ConsensusLog> findByLogIndex(Long logIndex) {
        return reactiveMongoTemplate.
                find(Query.query(where("logIndex").is(logIndex)), ConsensusLog.class).next();
    }

    public Mono<ConsensusLog> findMaxIndex() {
        return consensusLogRepository.
                findAll(Sort.by(Sort.Direction.DESC, "logIndex")).next();
    }
}
