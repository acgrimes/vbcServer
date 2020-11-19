package com.dd.vbc.business.services.server.consensus.follower;

import com.dd.vbc.business.services.server.blockchain.BlockChainService;
import com.dd.vbc.business.services.server.consensus.ConsensusService;
import com.dd.vbc.business.services.server.election.ElectionService;
import com.dd.vbc.domain.AppendEntry;
import com.dd.vbc.domain.BlockChainMetadata;
import com.dd.vbc.domain.ConsensusServer;
import com.dd.vbc.domain.Voter;
import com.dd.vbc.enums.Response;
import com.dd.vbc.enums.ReturnCode;
import com.dd.vbc.messageService.request.ConsensusRequest;
import com.dd.vbc.messageService.request.ElectionRequest;
import com.dd.vbc.messageService.request.HeartBeatRequest;
import com.dd.vbc.messageService.response.ConsensusResponse;
import com.dd.vbc.messageService.response.GeneralResponse;
import com.dd.vbc.messageService.response.HeartBeatResponse;
import com.dd.vbc.utils.BuildElectionRequest;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FollowerResponseHandlerMockTest {

    private static final Logger log = LoggerFactory.getLogger(FollowerResponseHandlerMockTest.class);

    @MockBean
    private BlockChainService blockChainService;

    @MockBean
    private ElectionService electionService;

    @MockBean
    private ConsensusService consensusService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void commitEntryFollowerResponseTest() throws Exception {

        ElectionRequest electionRequest = BuildElectionRequest.build();
        byte[] currentHash = SerializationUtils.serialize(electionRequest.getElectionTransaction());
        AppendEntry appendEntry = new AppendEntry(ConsensusServer.getServerInstance(),
                                                BlockChainMetadata.getActiveBlock().get(),
                                                BlockChainMetadata.getActiveBlockTxCount().get(),
                                                electionRequest.getElectionTransaction());
        appendEntry.setvToken(electionRequest.getVoter().getVtoken());
        appendEntry.setCommitted(false);
        appendEntry.setBlockChainHash(currentHash);

        ConsensusRequest consensusRequest = new ConsensusRequest(appendEntry);
        Mockito.when(blockChainService.followerCommitEntryResponse(appendEntry)).thenReturn(Mono.just(appendEntry));

        webTestClient.
            post().
            uri("/consensus/commitEntry").
            bodyValue(consensusRequest).
            accept(MediaType.APPLICATION_JSON).
            exchange().
            expectStatus().isOk().
            expectHeader().contentType(MediaType.APPLICATION_JSON).
            expectBody(ConsensusResponse.class).
            consumeWith(result -> {
                Assert.assertEquals(ReturnCode.SUCCESS, result.getResponseBody().getReturnCode());
                Assert.assertEquals(HttpStatus.OK, result.getResponseBody().getHttpStatus());
            });
    }

    @Test
    public void electionTransactionTest() throws Exception {

        ElectionRequest electionRequest = BuildElectionRequest.build();
        byte[] currentHash = SerializationUtils.serialize(electionRequest.getElectionTransaction());
        AppendEntry appendEntry = new AppendEntry(ConsensusServer.getServerInstance(),
                BlockChainMetadata.getActiveBlock().get(),
                BlockChainMetadata.getActiveBlockTxCount().get(),
                electionRequest.getElectionTransaction());
        appendEntry.setvToken(electionRequest.getVoter().getVtoken());
        appendEntry.setCommitted(false);
        appendEntry.setBlockChainHash(currentHash);

        GeneralResponse generalResponse = new GeneralResponse(new Voter(), ReturnCode.SUCCESS, Response.BallotAccepted);
        Mockito.when(electionService.electionTransactionResponse(electionRequest)).thenReturn(Mono.just(generalResponse));

        webTestClient.
            post().
            uri("/election/transaction").
            bodyValue(electionRequest).
            accept(MediaType.APPLICATION_JSON).
            exchange().
            expectStatus().isOk().
            expectHeader().contentType(MediaType.APPLICATION_JSON).
            expectBody(GeneralResponse.class).
            consumeWith(result -> {
                Assert.assertEquals(ReturnCode.SUCCESS, result.getResponseBody().getReturnCode());
                Assert.assertEquals(Response.BallotAccepted, result.getResponseBody().getResponse());
            });
    }

    @Test
    public void followerHeartbeatTest() throws Exception {

        ElectionRequest electionRequest = BuildElectionRequest.build();
        byte[] currentHash = SerializationUtils.serialize(electionRequest.getElectionTransaction());
        AppendEntry appendEntry = new AppendEntry(ConsensusServer.getServerInstance(),
                BlockChainMetadata.getActiveBlock().get(),
                BlockChainMetadata.getActiveBlockTxCount().get(),
                electionRequest.getElectionTransaction());
        appendEntry.setvToken(electionRequest.getVoter().getVtoken());
        appendEntry.setCommitted(false);
        appendEntry.setBlockChainHash(currentHash);

        HeartBeatRequest heartBeatRequest = new HeartBeatRequest(appendEntry);

        Mockito.when(consensusService.followerHeartbeatResponse(appendEntry)).thenReturn(Mono.just(appendEntry));

        webTestClient.
            post().
            uri("/consensus/heartbeat").
            bodyValue(heartBeatRequest).
            accept(MediaType.APPLICATION_JSON).
            exchange().
            expectStatus().isOk().
            expectHeader().contentType(MediaType.APPLICATION_JSON).
            expectBody(HeartBeatResponse.class).
            consumeWith(result -> {
                Assert.assertEquals(appendEntry, result.getResponseBody().getAppendEntry());
            });
    }

    @Test
    public void followerLogEntryTest() throws Exception {

        ElectionRequest electionRequest = BuildElectionRequest.build();
        byte[] currentHash = SerializationUtils.serialize(electionRequest.getElectionTransaction());
        AppendEntry appendEntry = new AppendEntry(ConsensusServer.getServerInstance(),
                BlockChainMetadata.getActiveBlock().get(),
                BlockChainMetadata.getActiveBlockTxCount().get(),
                electionRequest.getElectionTransaction());
        appendEntry.setvToken(electionRequest.getVoter().getVtoken());
        appendEntry.setCommitted(false);
        appendEntry.setBlockChainHash(currentHash);

        ConsensusRequest consensusRequest = new ConsensusRequest(appendEntry);

        Mockito.when(consensusService.followerLogEntryResponse(appendEntry)).thenReturn(Mono.just(appendEntry));

        webTestClient.
            post().
            uri("/consensus/logEntry").
            bodyValue(consensusRequest).
            accept(MediaType.APPLICATION_JSON).
            exchange().
            expectStatus().isOk().
            expectHeader().contentType(MediaType.APPLICATION_JSON).
            expectBody(ConsensusResponse.class).
            consumeWith(result -> {
                Assert.assertEquals(ReturnCode.SUCCESS, result.getResponseBody().getReturnCode());
                Assert.assertEquals(HttpStatus.OK, result.getResponseBody().getHttpStatus());
            });
    }
}
