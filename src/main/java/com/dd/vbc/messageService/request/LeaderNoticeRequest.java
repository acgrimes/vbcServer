package com.dd.vbc.messageService.request;

import com.dd.vbc.domain.Server;
import com.dd.vbc.enums.Request;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * This class is used to send notifications to the proxy server indicating which blockchain server is the leader. When
 * a blockchain server becomes a leader that server needs to notify the proxy server of this change.
 */
public class LeaderNoticeRequest implements Serializable {

    private Request request;
    private Server server;

    public LeaderNoticeRequest() {}
    public LeaderNoticeRequest(Request request, Server server) {
        this.request = request;
        this.server = server;
    }

    public Request getRequest() {
        return request;
    }

    public Server getServer() {
        return server;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        LeaderNoticeRequest that = (LeaderNoticeRequest) o;

        return new EqualsBuilder()
                .append(request, that.request)
                .append(server, that.server)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(request)
                .append(server)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "LeaderNoticeRequest{" +
                "request=" + request +
                ", server=" + server +
                '}';
    }
}
