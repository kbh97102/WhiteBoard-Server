/*
 * ReceivedData.java
 * Author : Arakene
 * Created Date : 2020-02-14
 */
package com.thunder_cut.server.data;

import com.thunder_cut.server.ClientInfo;

import java.nio.ByteBuffer;

public class ReceivedData {

    private DataType dataType;
    private ByteBuffer buffer;
    private ClientInfo src;

    public ReceivedData(ClientInfo src, DataType dataType, ByteBuffer buffer) {
        this.src = src;
        this.dataType = dataType;
        this.buffer = buffer;
    }

    public ClientInfo getSrc() {
        return src;
    }

    public DataType getDataType() {
        return dataType;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
