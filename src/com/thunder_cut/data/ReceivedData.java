/*
 * ReceivedData.java
 * Author : Arakene
 * Created Date : 2020-02-24
 */
package com.thunder_cut.data;

import java.nio.ByteBuffer;

public class ReceivedData {

    private ByteBuffer buffer;
    private DataType dataType;
    private int srcID;
    private String name;

    public ReceivedData(DataType dataType, ByteBuffer buffer, int srcID, String name) {
        this.buffer = buffer;
        this.dataType = dataType;
        this.srcID = srcID;
        this.name = name;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public DataType getDataType() {
        return dataType;
    }

    public int getSrcID() {
        return srcID;
    }

    public String getName() {
        return name;
    }
}
