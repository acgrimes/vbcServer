package com.dd.vbc.utils;

import com.dd.vbc.dao.blockChain.MerkleTreeDao;
import com.dd.vbc.db.mongo.MongoConfig;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {MongoConfig.class, MerkleTreeDao.class})
public class GenerateObjectIdTest {

    @Test
    public void testComputingObjectId() {

        ObjectId objectId = GenerateObjectId.generateObjectId();
        ObjectId expectedObjectId = new ObjectId("000000000000000000000001");
        assertEquals(objectId, expectedObjectId);

    }
}
