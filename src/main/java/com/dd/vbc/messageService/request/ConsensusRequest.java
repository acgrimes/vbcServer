package com.dd.vbc.messageService.request;

import com.dd.vbc.domain.AppendEntry;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ConsensusRequest implements Serializable {

    private AppendEntry appendEntry;

    public ConsensusRequest() {}
    public ConsensusRequest(AppendEntry appendEntry) {
        this.appendEntry = appendEntry;
    }

    public AppendEntry getAppendEntry() {
        return appendEntry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ConsensusRequest that = (ConsensusRequest) o;

        return new EqualsBuilder()
                .append(appendEntry, that.appendEntry)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(appendEntry)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ConsensusRequest{" +
                "appendEntry=" + appendEntry +
                '}';
    }
}
