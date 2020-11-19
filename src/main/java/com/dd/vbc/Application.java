package com.dd.vbc;

import com.dd.vbc.business.services.client.consensus.scheduling.ScheduleConfiguration;
import com.dd.vbc.business.services.client.consensus.scheduling.Scheduler;
import com.dd.vbc.db.mongo.MongoConfig;
import com.dd.vbc.domain.BlockChainMetadata;
import com.dd.vbc.domain.ConsensusServer;
import com.dd.vbc.domain.ConsensusState;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.security.Security;
import java.util.concurrent.atomic.AtomicLong;


@SpringBootApplication(scanBasePackageClasses = {},
                        scanBasePackages = {"com.dd.vbc",
                                            "com.dd.vbc.messageService.httpClient",
                                            "com.dd.vbc.dao.blockChain.repository",
                                           "com.dd.vbc.dao.consensus.repository"})
@Import({MongoConfig.class, ScheduleConfiguration.class})
public class Application implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private String args = "A";

    private Scheduler scheduler;

    @Autowired
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public static void main(String[] args) throws Exception {

        ConsensusServer.setId(args[0]);
        SpringApplication.run(Application.class, args);

    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {

        ConsensusState.initializeServerList();
        ConsensusState.initializeProxyServer();

        if(applicationArguments.getSourceArgs().length!=0) {
            args = applicationArguments.getSourceArgs()[0];
        }

        ConsensusState.getServerList().stream().
                forEach(serv -> {
                    if(serv.getId().equals(args)) {
                        ConsensusServer.setId(serv.getId());
                        ConsensusServer.setHost(serv.getHost());
                        ConsensusServer.setHttpPort(serv.getHttpPort());
                        ConsensusServer.setReactivePort(serv.getReactivePort());
                        ConsensusServer.setState(serv.getState());
                    }
                });
        log.debug("Server Id is: "+ConsensusServer.getId());

        //TODO: these values need to be read from database.
        ConsensusState.setCurrentIndex(new AtomicLong(0L));
        ConsensusState.setCurrentTerm(new AtomicLong(1L));

        //TODO: these values need to be read from database:
        BlockChainMetadata.setActiveBlock(new AtomicLong(0L));
        BlockChainMetadata.setActiveBlockTxCount(new AtomicLong(0L));

        int position = Security.addProvider(new BouncyCastleProvider());
        log.debug("Provider position is: "+position);
        scheduler.startFollowerHeartBeatTimeoutTimer();

    }
}
