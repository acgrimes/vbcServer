package com.dd.vbc.business.services.server.blockchain;

import com.dd.vbc.business.services.client.consensus.leader.LeaderCommitEntryRequest;
import com.dd.vbc.domain.AppendEntry;
import com.dd.vbc.domain.ConsensusServer;
import com.dd.vbc.domain.ConsensusState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

@Component
public class BlockChainServiceEvent implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(BlockChainServiceEvent.class);

    private AppendEntry appendEntry;
    public void setAppendEntry(AppendEntry appendEntry) {
        this.appendEntry = appendEntry;
    }

    private BlockChainService blockChainService;
    @Autowired
    public void setBlockChainService(BlockChainService blockChainService) {
        this.blockChainService = blockChainService;
    }

    private LeaderCommitEntryRequest leaderCommitEntryRequest;
    @Autowired
    public void setLeaderCommitEntryRequest(LeaderCommitEntryRequest leaderCommitEntryRequest) {
        this.leaderCommitEntryRequest = leaderCommitEntryRequest;
    }

    private ThreadPoolExecutor executor;
    @Autowired
    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void run() {

        Consumer<AppendEntry> onSuccess = (AppendEntry appendEntry) -> {
            ConsensusState.getLeaderCommitList().set(appendEntry.getIndex().intValue(), Boolean.TRUE);
            appendEntry.setServer(ConsensusServer.getServerInstance());
            leaderCommitEntryRequest.setAppendEntry(appendEntry);
            executor.execute(leaderCommitEntryRequest);
        };
        Consumer<Throwable> onError = Throwable::getMessage;
        Runnable onCompletion = () -> { if(log.isDebugEnabled()) log.debug("onApplicationEvent: Message Completed"); };

        blockChainService.followerCommitEntryResponse(appendEntry).
                subscribe(onSuccess, onError, onCompletion);

    }

}
