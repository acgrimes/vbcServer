package com.dd.vbc.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Arrays;
import java.util.UUID;

public class AppendEntry implements DomainObject {

    private Server server;
    private UUID vToken;
    private Long index;
    private Long term;
    private Long prevLogIndex;
    private Long prevLogTerm;
    private Long leaderCommitIndex;
    private Boolean log = Boolean.FALSE;
    private Boolean logged = Boolean.FALSE;
    private Boolean commit = Boolean.FALSE;
    private Boolean committed = Boolean.FALSE;
    private byte[] electionTransaction;
    private byte[] blockChainHash;

    public AppendEntry() {
    }

    public AppendEntry(Server server, Long index, Long term, byte[] electionTransaction) {
        this.server = server;
        this.index = index;
        this.term = term;
        this.electionTransaction = electionTransaction;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public UUID getvToken() {
        return vToken;
    }

    public void setvToken(UUID vToken) {
        this.vToken = vToken;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public Long getTerm() {
        return term;
    }

    public void setTerm(Long term) {
        this.term = term;
    }

    public Long getPrevLogIndex() {
        return prevLogIndex;
    }

    public void setPrevLogIndex(Long prevLogIndex) {
        this.prevLogIndex = prevLogIndex;
    }

    public Long getPrevLogTerm() {
        return prevLogTerm;
    }

    public void setPrevLogTerm(Long prevLogTerm) {
        this.prevLogTerm = prevLogTerm;
    }

    public Long getLeaderCommitIndex() {
        return leaderCommitIndex;
    }

    public void setLeaderCommitIndex(Long leaderCommitIndex) {
        this.leaderCommitIndex = leaderCommitIndex;
    }

    public Boolean getLog() {
        return log;
    }

    public void setLog(Boolean log) {
        this.log = log;
    }

    public Boolean getLogged() {
        return logged;
    }

    public void setLogged(Boolean logged) {
        this.logged = logged;
    }

    public Boolean getCommit() {
        return commit;
    }

    public void setCommit(Boolean commit) {
        this.commit = commit;
    }

    public Boolean getCommitted() {
        return committed;
    }

    public void setCommitted(Boolean committed) {
        this.committed = committed;
    }

    public byte[] getElectionTransaction() {
        return electionTransaction;
    }

    public void setElectionTransaction(byte[] electionTransaction) {
        this.electionTransaction = electionTransaction;
    }

    public byte[] getBlockChainHash() {
        return blockChainHash;
    }

    public void setBlockChainHash(byte[] blockChainHash) {
        this.blockChainHash = blockChainHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AppendEntry entry = (AppendEntry) o;

        return new EqualsBuilder()
                .append(server, entry.server)
                .append(vToken, entry.vToken)
                .append(index, entry.index)
                .append(term, entry.term)
                .append(prevLogIndex, entry.prevLogIndex)
                .append(prevLogTerm, entry.prevLogTerm)
                .append(leaderCommitIndex, entry.leaderCommitIndex)
                .append(log, entry.log)
                .append(logged, entry.logged)
                .append(commit, entry.commit)
                .append(committed, entry.committed)
                .append(electionTransaction, entry.electionTransaction)
                .append(blockChainHash, entry.blockChainHash)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(server)
                .append(vToken)
                .append(index)
                .append(term)
                .append(prevLogIndex)
                .append(prevLogTerm)
                .append(leaderCommitIndex)
                .append(log)
                .append(logged)
                .append(commit)
                .append(committed)
                .append(electionTransaction)
                .append(blockChainHash)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "AppendEntry{" +
                "server=" + server +
                ", vToken=" + vToken +
                ", index=" + index +
                ", term=" + term +
                ", prevLogIndex=" + prevLogIndex +
                ", prevLogTerm=" + prevLogTerm +
                ", leaderCommitIndex=" + leaderCommitIndex +
                ", log=" + log +
                ", logged=" + logged +
                ", commit=" + commit +
                ", committed=" + committed +
                ", electionTransaction=" + Arrays.toString(electionTransaction) +
                ", blockChainHash=" + Arrays.toString(blockChainHash) +
                '}';
    }
}