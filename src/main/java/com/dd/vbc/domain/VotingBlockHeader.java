package com.dd.vbc.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Arrays;
import java.util.Date;

public class VotingBlockHeader implements DomainObject {

    private Version version;
    private Long txCount;
    private byte[] previousBlockHash;
    private byte[] merkleRoot;
    private Date dateTime;

    public VotingBlockHeader() {}
    public VotingBlockHeader(Version version, Long txCount, byte[] previousBlockHash, byte[] merkleRoot, Date dataTime) {
        this.version = version;
        this.txCount = txCount;
        this.previousBlockHash = previousBlockHash;
        this.merkleRoot = merkleRoot;
        this.dateTime = dataTime;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Long getTxCount() {
        return txCount;
    }

    public void setTxCount(Long txCount) {
        this.txCount = txCount;
    }

    public byte[] getPreviousBlockHash() {
        return previousBlockHash;
    }

    public void setPreviousBlockHash(byte[] previousBlockHash) {
        this.previousBlockHash = previousBlockHash;
    }

    public byte[] getMerkleRoot() {
        return merkleRoot;
    }

    public void setMerkleRoot(byte[] merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        VotingBlockHeader that = (VotingBlockHeader) o;

        return new EqualsBuilder()
                .append(version, that.version)
                .append(txCount, that.txCount)
                .append(previousBlockHash, that.previousBlockHash)
                .append(merkleRoot, that.merkleRoot)
                .append(dateTime, that.dateTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(version)
                .append(txCount)
                .append(previousBlockHash)
                .append(merkleRoot)
                .append(dateTime)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "VotingBlockHeader{" +
                "version=" + version +
                ", txCount=" + txCount +
                ", previousBlockHash=" + Arrays.toString(previousBlockHash) +
                ", merkleRoot=" + Arrays.toString(merkleRoot) +
                ", dateTime=" + dateTime +
                '}';
    }
}
