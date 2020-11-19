package com.dd.vbc.dao.blockChain;

import com.dd.vbc.dao.blockChain.repository.VoterTokenBlockMapRepository;
import com.dd.vbc.domain.VoterTokenBlockMap;
import com.mongodb.client.result.DeleteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class VoterTokenBlockMapDao {

    private static final Logger log = LoggerFactory.getLogger(VoterTokenBlockMapDao.class);

    private VoterTokenBlockMapRepository voterTokenBlockMapRepository;

    @Autowired
    public void setVoterTokenBlockMapRepository(VoterTokenBlockMapRepository voterTokenBlockMapRepository) {
        this.voterTokenBlockMapRepository = voterTokenBlockMapRepository;
    }

    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    public void setReactiveMongoTemplate(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    /**
     *
     * @param voterTokenBlockMap - mapping object from UUID to VoterTokenBlockMap
     * @return - the VoterTokenBlockMap object saved to database
     */
    public Mono<VoterTokenBlockMap> save(final VoterTokenBlockMap voterTokenBlockMap) {

        return voterTokenBlockMapRepository.save(voterTokenBlockMap)
                .doOnSuccess(success -> log.debug("Saved: "+voterTokenBlockMap.toString()))
                .doOnError(Throwable::printStackTrace);
    }

    /**
     *
     * @param voterTokenBlockMap
     * @return
     */
    public Mono<VoterTokenBlockMap> insert(final VoterTokenBlockMap voterTokenBlockMap) {
        return voterTokenBlockMapRepository.save(voterTokenBlockMap)
                .doOnSuccess(success -> log.debug("insert Saved: "+voterTokenBlockMap.toString()))
                .doOnError(Throwable::printStackTrace);
    }

    /**
     *
     * @param id - UUID associated with the VoterTokenBlockMap object
     * @return - the VoterTokenBlockMap object associated with the UUID
     */
    public Mono<VoterTokenBlockMap> findById(UUID id) {

        return voterTokenBlockMapRepository.findById(id);
    }

    /**
     *
     * @param id
     * @return
     */
    public Mono<Boolean> existsById(UUID id) {
        return voterTokenBlockMapRepository.existsById(id);
    }

    /**
     *
     * @param id
     * @return
     */
    public Mono<Void> deleteById(UUID id) {
        return voterTokenBlockMapRepository.deleteById(id);
    }

    /**
     *
     * @param voterTokenBlockMap
     * @return
     */
    public Mono<DeleteResult> delete(final VoterTokenBlockMap voterTokenBlockMap) {
        return this.reactiveMongoTemplate
                .remove(voterTokenBlockMap, "voterTokenBlockMap")
                .doOnSuccess(success -> log.debug("delete remove: "+voterTokenBlockMap.toString()))
                .doOnError(Throwable::printStackTrace);
    }
}
