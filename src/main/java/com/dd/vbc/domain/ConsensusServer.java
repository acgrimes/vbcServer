package com.dd.vbc.domain;

import com.dd.vbc.enums.ServerConsensusState;

public class ConsensusServer {

    private static String id = "A";
    private static String host;
    private static int httpPort;
    private static int reactivePort;
    private static int leaderVotes;
    private static ServerConsensusState state;
    private static String databaseName;


    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        ConsensusServer.id = id;
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        ConsensusServer.host = host;
    }

    public static int getHttpPort() {
        return httpPort;
    }

    public static void setHttpPort(int httpPort) {
        ConsensusServer.httpPort = httpPort;
    }

    public static int getReactivePort() {
        return reactivePort;
    }

    public static void setReactivePort(int reactivePort) {
        ConsensusServer.reactivePort = reactivePort;
    }

    public static int getLeaderVotes() {
        return leaderVotes;
    }

    public static void setLeaderVotes(int leaderVotes) {
        ConsensusServer.leaderVotes = leaderVotes;
    }

    public static ServerConsensusState getState() {
        return state;
    }

    public static void setState(ServerConsensusState state) {
        ConsensusServer.state = state;
    }

    public static String getDatabaseName() {
        return databaseName;
    }

    public static void setDatabaseName(String databaseName) {
        ConsensusServer.databaseName = databaseName;
    }

    public static Server getServerInstance() {
        return new Server(id, host, httpPort, reactivePort, state);
    }

}
