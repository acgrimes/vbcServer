package com.dd.vbc.enums;

import com.dd.vbc.domain.Serialization;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static com.dd.vbc.utils.SerialUtil.byteArrayToInt;

public enum Request implements Serializable {
    Login,
    Heartbeat,
    Authentication,
    BallotRequest,
    ElectionTransaction,
    ViewBallot,
    LeaderNotification,
    LeaderVote,
    Last;

    private static final Map<Integer, Request> lookup = new HashMap<Integer, Request>();

    static{
        int ordinal = 0;
        for (Request request : EnumSet.allOf(Request.class)) {
            lookup.put(ordinal, request);
            ordinal+= 1;
        }
    }

    public static Request fromOrdinal(int ordinal) {
        return lookup.get(ordinal);
    }

    private static Request setRequest(byte[] message) {

        byte[] requestTypeLength = new byte[4];
        for(int i=0; i<4; i++) {
            requestTypeLength[i] = message[i];
        }
        int ord = byteArrayToInt(requestTypeLength);
        return Request.fromOrdinal(ord);
    }

    public byte[] serialize() {
        Serialization serial = new Serialization();
        byte[] requestByte = serial.serializeInt(this.ordinal());
        return requestByte;
    }

    public static Request deserialize(byte[] request) {
        Serialization serial = new Serialization();
        int enumValue = serial.deserializeInt(request);
        return Request.fromOrdinal(enumValue);
    }
}
