package com.dd.vbc.utils;

import com.dd.vbc.domain.Version;
import com.dd.vbc.domain.VotingBlockHeader;
import com.dd.vbc.domain.VotingTxBlock;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BuildVotingTxBlock {

    public static VotingTxBlock build(Long blockId, Long txCount, byte[] hash) {

        Version version = new Version(1, 2);
        VotingBlockHeader header = new VotingBlockHeader(version, txCount, hash, hash, new Date());
        UUID id = UUID.randomUUID();
        Map<UUID, byte[]> txMap = new HashMap<>();
        txMap.put(id, hash);

        return new VotingTxBlock(blockId, header, txMap);
    }
}
