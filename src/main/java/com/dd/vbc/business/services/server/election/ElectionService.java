package com.dd.vbc.business.services.server.election;

import com.dd.vbc.business.services.client.consensus.leader.LeaderLogEntryRequest;
import com.dd.vbc.business.services.client.consensus.leader.events.LogEntryEvent;
import com.dd.vbc.dao.consensus.ConsensusLogDao;
import com.dd.vbc.domain.AppendEntry;
import com.dd.vbc.domain.ConsensusLog;
import com.dd.vbc.domain.ConsensusServer;
import com.dd.vbc.domain.ConsensusState;
import com.dd.vbc.enums.Response;
import com.dd.vbc.enums.ReturnCode;
import com.dd.vbc.messageService.request.ElectionRequest;
import com.dd.vbc.messageService.response.GeneralResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ElectionService {

    private static final Logger log = LoggerFactory.getLogger(ElectionService.class);

    private ConsensusLogDao consensusLogDao;
    @Autowired
    public void setConsensusLogDao(ConsensusLogDao consensusLogDao) {
        this.consensusLogDao = consensusLogDao;
    }

    private ThreadPoolExecutor executor;
    @Autowired
    public void setThreadPoolExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    private LeaderLogEntryRequest leaderLogEntryRequest;
    @Autowired
    public void setLeaderLogEntryRequest(LeaderLogEntryRequest leaderLogEntryRequest) {
        this.leaderLogEntryRequest = leaderLogEntryRequest;
    }
    /**
     *
     * @param electionRequest
     * @return
     */
    public Mono<GeneralResponse> electionTransactionResponse(ElectionRequest electionRequest) {

        GeneralResponse generalResponse = new GeneralResponse(electionRequest.getVoter(),
                                                              ReturnCode.FAILURE, Response.BallotInvalid);

        if(validateElectionRequest(electionRequest)) {
            ConsensusState.setCurrentIndex(new AtomicLong(ConsensusState.getCurrentIndex().incrementAndGet()));
            ConsensusLog consensusLog = new ConsensusLog(ConsensusState.getCurrentIndex().get(),
                                                        ConsensusState.getCurrentTerm().get(),
                                                        electionRequest.getElectionTransaction());
            AppendEntry appendEntry = new AppendEntry(ConsensusServer.getServerInstance(),
                                                        ConsensusState.getCurrentIndex().get(),
                                                        ConsensusState.getCurrentTerm().get(),
                                                        electionRequest.getElectionTransaction());
            appendEntry.setvToken(electionRequest.getVoter().getVtoken());
            return consensusLogDao.
                save(consensusLog).
                flatMap(cl -> Mono.just(appendEntry)).
                    doOnNext((ap) -> {
                        leaderLogEntryRequest.setAppendEntry(ap);
                        executor.execute(leaderLogEntryRequest);
                    }).
                flatMap(ap -> Mono.just(generalResponse)).
                doOnSuccess(gr -> {
                    gr.setReturnCode(ReturnCode.SUCCESS);
                    gr.setResponse(Response.BallotAccepted);
                    if(log.isDebugEnabled()) log.debug("electionTransactionResponse(): "+gr.toString());
                }).
                doOnError(ex -> new Throwable("Election Request Failed"));
        } else {
            return Mono.just(generalResponse);
        }
    }

    /**
     *
     * @param electionRequest
     * @return
     */
    private boolean validateElectionRequest(ElectionRequest electionRequest) {

        boolean result = false;
        byte[] electionTx = electionRequest.getElectionTransaction();
        byte[] encodedPubKey = electionRequest.getPublicKey();
        byte[] txSignature = electionRequest.getDigitalSignature();

        ValidateDigitalSignature signature = new ValidateDigitalSignature();
        if(signature.isValid(electionTx, encodedPubKey, txSignature)) {
            result = true;
        }
        return result;

    }
}
