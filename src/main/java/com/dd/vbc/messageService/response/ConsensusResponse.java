package com.dd.vbc.messageService.response;


import com.dd.vbc.enums.ReturnCode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

public class ConsensusResponse<Message> implements Serializable {

    private HttpStatus httpStatus;
    private ReturnCode returnCode;
    private Message response;

    public ConsensusResponse() {}
    public ConsensusResponse(HttpStatus httpStatus, ReturnCode returnCode, Message message) {
        this.httpStatus = httpStatus;
        this.returnCode = returnCode;
        this.response = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ReturnCode getReturnCode() {
        return returnCode;
    }

    public Message getResponse() {
        return response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ConsensusResponse<?> that = (ConsensusResponse<?>) o;

        return new EqualsBuilder()
                .append(httpStatus, that.httpStatus)
                .append(returnCode, that.returnCode)
                .append(response, that.response)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(httpStatus)
                .append(returnCode)
                .append(response)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ConsensusResponse{" +
                "httpStatus=" + httpStatus +
                ", returnCode=" + returnCode +
                ", response=" + response +
                '}';
    }
}
