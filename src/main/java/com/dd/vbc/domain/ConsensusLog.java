package com.dd.vbc.domain;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.Objects;

@Document
public class ConsensusLog implements DomainObject, Comparable {

    @Id
    private Long logIndex;
    private Long logTerm;
    private byte[] electionTransaction;

    public ConsensusLog(Long logIndex, Long logTerm, byte[] electionTransaction) {
        this.logIndex = logIndex;
        this.logTerm = logTerm;
        this.electionTransaction = electionTransaction;
    }

    public Long getLogIndex() {
        return logIndex;
    }

    public Long getLogTerm() {
        return logTerm;
    }

    public byte[] getElectionTransaction() {
        return electionTransaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsensusLog that = (ConsensusLog) o;
        return logIndex.equals(that.logIndex) &&
                logTerm.equals(that.logTerm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logIndex, logTerm);
    }

    public int compareTo(Object o) {
        ConsensusLog myClass = (ConsensusLog) o;
        return new CompareToBuilder()
            .append(this.logIndex, myClass.logIndex)
            .toComparison();
    }

    @Override
    public String toString() {
        return "ConsensusLog{" +
                "logIndex=" + logIndex +
                ", logTerm=" + logTerm +
                ", electionTransaction=" + Arrays.toString(electionTransaction) +
                '}';
    }
}
