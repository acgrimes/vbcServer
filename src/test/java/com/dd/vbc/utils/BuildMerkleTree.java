package com.dd.vbc.utils;

import com.dd.vbc.domain.MerkleTree;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BuildMerkleTree {

    public static MerkleTree build(Long blockId, Long txCount, byte[] hash) {

        Map<UUID, byte[]> nodeMap = new HashMap<>();
        for(long i=0; i<txCount; i++) {
            nodeMap.put(UUID.randomUUID(), hash);
        }
        MerkleTree merkleTree = new MerkleTree(blockId, txCount, hash, nodeMap);
        return merkleTree;
    }

}
