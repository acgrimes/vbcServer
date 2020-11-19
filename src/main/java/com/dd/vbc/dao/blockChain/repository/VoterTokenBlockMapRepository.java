package com.dd.vbc.dao.blockChain.repository;

import com.dd.vbc.domain.VoterTokenBlockMap;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Primary
@Repository
public interface VoterTokenBlockMapRepository extends ReactiveMongoRepository<VoterTokenBlockMap, UUID> {}
