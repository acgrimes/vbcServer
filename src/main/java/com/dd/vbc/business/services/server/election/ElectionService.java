package com.dd.vbc.business.services.server.election;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class ElectionService {

    @Autowired
    private ConsensusLogDao consensusLogDao;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

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
                doOnNext((ap) -> applicationEventPublisher.publishEvent(new LogEntryEvent(ap))).
                flatMap(ap -> Mono.just(generalResponse)).
                doOnSuccess(gr -> {
                    gr.setReturnCode(ReturnCode.SUCCESS);
                    gr.setResponse(Response.BallotAccepted);
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
