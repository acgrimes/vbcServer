package com.dd.vbc.utils;

import com.dd.vbc.domain.ElectionTransaction;
import com.dd.vbc.domain.LoginCredentials;
import com.dd.vbc.domain.Voter;
import com.dd.vbc.domain.VotingDistrict;
import com.dd.vbc.enums.Request;
import com.dd.vbc.messageService.request.ElectionRequest;
import org.apache.commons.lang3.SerializationUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyPair;
import java.security.Security;
import java.util.UUID;

public class BuildElectionRequest {

    public static ElectionRequest build() {

        ElectionTransaction electionTransaction = BuildElectionTransaction.build();

        Security.addProvider(new BouncyCastleProvider());

        byte[] et = SerializationUtils.serialize(electionTransaction);
        VotingDistrict votingDistrict = new VotingDistrict(1,1,1,"",1,1,1);
        Voter voter = new Voter(2L, UUID.randomUUID(), votingDistrict);

        KeyPair keyPair = Utils.generateECDSAKeyPair();
        byte[] signedTx = Utils.digitalSignature(keyPair.getPrivate(), et);

        LoginCredentials lc = new LoginCredentials("user", "password");

        ElectionRequest electionRequest = new ElectionRequest(Request.Heartbeat, lc, voter, et, keyPair.getPublic().getEncoded(), signedTx);

        return electionRequest;

    }

}
