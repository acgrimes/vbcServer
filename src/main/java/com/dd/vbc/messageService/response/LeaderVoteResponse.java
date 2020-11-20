package com.dd.vbc.messageService.response;

import com.dd.vbc.domain.RequestLeaderVote;
import com.dd.vbc.enums.ReturnCode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class LeaderVoteResponse implements Serializable {

    private ReturnCode returnCode;
    private RequestLeaderVote requestLeaderVote;

    public LeaderVoteResponse() {}
    public LeaderVoteResponse(ReturnCode returnCode, RequestLeaderVote requestLeaderVote) {
        this.returnCode = returnCode;
        this.requestLeaderVote = requestLeaderVote;
    }

    public ReturnCode getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(ReturnCode returnCode) {
        this.returnCode = returnCode;
    }

    public RequestLeaderVote getRequestLeaderVote() {
        return requestLeaderVote;
    }

    public void setRequestLeaderVote(RequestLeaderVote requestLeaderVote) {
        this.requestLeaderVote = requestLeaderVote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        LeaderVoteResponse response = (LeaderVoteResponse) o;

        return new EqualsBuilder()
                .append(returnCode, response.returnCode)
                .append(requestLeaderVote, response.requestLeaderVote)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(returnCode)
                .append(requestLeaderVote)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "LeaderVoteResponse{" +
                "returnCode=" + returnCode +
                ", requestLeaderVote=" + requestLeaderVote +
                '}';
    }
}
