package com.dd.vbc.messageService.request;

import com.dd.vbc.domain.RequestLeaderVote;
import com.dd.vbc.enums.Request;
import com.dd.vbc.messageService.response.LeaderVoteResponse;

import java.io.Serializable;

public class LeaderVoteRequest implements Serializable {

    private Request request;
    private RequestLeaderVote requestLeaderVote;

    public LeaderVoteRequest() {}
    public LeaderVoteRequest(Request request, RequestLeaderVote requestLeaderVote) {
        this.request = request;
        this.requestLeaderVote = requestLeaderVote;
    }

    public Request getRequest() {
        return request;
    }

    public RequestLeaderVote getRequestLeaderVote() {
        return requestLeaderVote;
    }
}
