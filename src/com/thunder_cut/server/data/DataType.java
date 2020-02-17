package com.thunder_cut.server.data;

/**
 * This type used to communication, server to client, client to server
 */
public enum DataType {
    IMG('I'), MSG('M'), CMD('C');

    public final char type;

    DataType(char type) {
        this.type = type;
    }
}
