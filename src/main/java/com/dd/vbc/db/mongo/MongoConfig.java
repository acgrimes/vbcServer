package com.dd.vbc.db.mongo;

import com.dd.vbc.dao.blockChain.repository.MerkleTreeRepository;
import com.dd.vbc.dao.blockChain.repository.VoterTokenBlockMapRepository;
import com.dd.vbc.dao.blockChain.repository.VotingTxBlockRepository;
import com.dd.vbc.dao.consensus.repository.ConsensusLogRepository;
import com.dd.vbc.domain.ConsensusServer;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.util.logging.Logger;

@Configuration
@EnableReactiveMongoRepositories
//        (basePackages={"com.dd.vbc.dao.consensus",
//                                                "com.dd.vbc.dao.consensus.repository",
//                                                "com.dd.vbc.dao.blockChain",
//                                                "com.dd.vbc.dao.blockChain.repository"})
        (basePackageClasses = {ConsensusLogRepository.class,
                                                MerkleTreeRepository.class,
                                                VoterTokenBlockMapRepository.class,
                                                VotingTxBlockRepository.class})
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    private final static Logger log = Logger.getLogger(MongoConfig.class.getSimpleName());

    private String databaseName;

    @Bean
    @Primary
    public ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory() {
        return new SimpleReactiveMongoDatabaseFactory(MongoClients.create("mongodb://localhost"), getDatabaseName());
    }

    @Bean
    public MongoClient reactiveMongoClient() {
        return MongoClients.create("mongodb://localhost");
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(reactiveMongoClient(), getDatabaseName());
    }

//    @Bean
//    public ReactiveMongoRepositoryFactory reactiveMongoRepositoryFactory() {
//        return new ReactiveMongoRepositoryFactory(reactiveMongoTemplate());
//    }
//
//    @Bean
//    public MerkleTreeRepository merkleTreeRepository() {
//        return reactiveMongoRepositoryFactory().getRepository(MerkleTreeRepository.class);
//    }
//
//    @Bean
//    public VotingTxBlockRepository votingTxBlockRepository() {
//        return reactiveMongoRepositoryFactory().getRepository(VotingTxBlockRepository.class);
//    }
//
//    @Bean
//    public VoterTokenBlockMapRepository voterTokenBlockMapRepository() {
//        return reactiveMongoRepositoryFactory().getRepository(VoterTokenBlockMapRepository.class);
//    }
//
//    @Bean
//    public ConsensusLogRepository consensusLogRepository() {
//        return reactiveMongoRepositoryFactory().getRepository(ConsensusLogRepository.class);
//    }

    @Override
    protected String getDatabaseName() {
        log.info("MongoDb database name: consensusLog"+ConsensusServer.getId());
        return "consensusLog"+ConsensusServer.getId();
    }

    protected String getMappingBasePackage() {
        return "com.dd.vbc.dao";
    }

//    public void setUp() throws Exception {
//        this.mongo = new MongoClient();
//        SimpleMongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(this.mongo, DATABASE_NAME);
//        MongoMappingContext context = new MongoMappingContext();
//        context.setInitialEntitySet(Collections.singleton(Person.class));
//        context.afterPropertiesSet();
//        this.converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), context);
//        this.operations = new MongoTemplate(new SimpleMongoDbFactory(this.mongo, DATABASE_NAME), converter);
//        MongoRepositoryFactoryBean<PersonRepository, Person, ObjectId> factory = new MongoRepositoryFactoryBean<PersonRepository, Person, ObjectId>(PersonRepository.class);
//        factory.setMongoOperations(operations);
//        factory.afterPropertiesSet();
//        this.repository = factory.getObject();
//    }
}
