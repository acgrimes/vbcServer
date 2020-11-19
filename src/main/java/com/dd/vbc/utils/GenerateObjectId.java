package com.dd.vbc.utils;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GenerateObjectId {

    private static final Logger log = LoggerFactory.getLogger(GenerateObjectId.class);
    private static long leaderLogIndex = 0;
    private static ObjectId currentObjectId = null;

    public static ObjectId generateObjectId() {

        leaderLogIndex++;
        String result = String.format("%24x", leaderLogIndex).replace(" ", "0");
        currentObjectId = new ObjectId(result);
        return currentObjectId;
    }

    public static ObjectId createObjectId(long id) {

        ObjectId oId = new ObjectId(String.format("%24x", id).replace(" ", "0"));
        log.info("ObjectId: "+oId);
        return oId;

    }
}
