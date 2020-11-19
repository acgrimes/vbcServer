package com.dd.vbc.messageService.request;

import com.dd.vbc.domain.LoginCredentials;
import com.dd.vbc.domain.Voter;
import com.dd.vbc.enums.Request;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Arrays;

public class ElectionRequest implements Serializable {

    private static final long serialVersionUID = 4L;

    private Request request;
    private LoginCredentials loginCredentials;
    private Voter voter;
    private byte[] electionTransaction;
    private byte[] publicKey;
    private byte[] digitalSignature;

    public ElectionRequest() {}
    public ElectionRequest(Request request,
                           LoginCredentials loginCredentials,
                           Voter voter,
                           byte[] electionTransaction,
                           byte[] publicKey,
                           byte[] digitalSignature) {
        this.request = request;
        this.loginCredentials = loginCredentials;
        this.voter = voter;
        this.electionTransaction = electionTransaction;
        this.publicKey = publicKey;
        this.digitalSignature = digitalSignature;
    }

    public ElectionRequest(MobileRequest mobileRequest) {
        this.request = mobileRequest.getRequest();
        this.loginCredentials = mobileRequest.getLoginCredentials();
        this.voter = mobileRequest.getVoter();
        this.electionTransaction = SerializationUtils.serialize(mobileRequest.getElectionTransaction());
        this.publicKey = mobileRequest.getPublicKey();
        this.digitalSignature = mobileRequest.getDigitalSignature();
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public LoginCredentials getLoginCredentials() {
        return loginCredentials;
    }

    public void setLoginCredentials(LoginCredentials loginCredentials) {
        this.loginCredentials = loginCredentials;
    }

    public Voter getVoter() {
        return voter;
    }

    public void setVoter(Voter voter) {
        this.voter = voter;
    }

    public byte[] getElectionTransaction() {
        return electionTransaction;
    }

    public void setElectionTransaction(byte[] electionTransaction) {
        this.electionTransaction = electionTransaction;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getDigitalSignature() {
        return digitalSignature;
    }

    public void setDigitalSignature(byte[] digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || this.getClass() != o.getClass()) return false;

        final ElectionRequest that = (ElectionRequest) o;

        return new EqualsBuilder()
                .append(this.request, that.request)
                .append(this.loginCredentials, that.loginCredentials)
                .append(this.voter, that.voter)
                .append(this.electionTransaction, that.electionTransaction)
                .append(this.publicKey, that.publicKey)
                .append(this.digitalSignature, that.digitalSignature)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(this.request)
                .append(this.loginCredentials)
                .append(this.voter)
                .append(this.electionTransaction)
                .append(this.publicKey)
                .append(this.digitalSignature)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ElectionRequest{" +
                "request=" + request +
                ", loginCredentials=" + loginCredentials +
                ", voter=" + voter +
                ", electionTransaction=" + Arrays.toString(electionTransaction) +
                ", publicKey=" + Arrays.toString(publicKey) +
                ", digitalSignature=" + Arrays.toString(digitalSignature) +
                '}';
    }
}
