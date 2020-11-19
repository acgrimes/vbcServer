package com.dd.vbc.messageService.response;

import com.dd.vbc.domain.RequestLeaderVote;
import com.dd.vbc.enums.ReturnCode;

import java.io.Serializable;

public class LeaderVoteResponse implements Serializable {

    private final ReturnCode returnCode;
    private final RequestLeaderVote requestLeaderVote;

    public LeaderVoteResponse(ReturnCode returnCode, RequestLeaderVote requestLeaderVote) {
        this.returnCode = returnCode;
        this.requestLeaderVote = requestLeaderVote;
    }

    public ReturnCode getReturnCode() {
        return returnCode;
    }

    public RequestLeaderVote getRequestLeaderVote() {
        return requestLeaderVote;
    }
}
