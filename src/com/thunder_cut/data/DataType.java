package com.thunder_cut.data;

public enum DataType {

    IMG('I'), MSG('M'), CMD('C');

    public final char type;

    DataType(char type) {
        this.type = type;
    }

    public static DataType valueOf(char name) {
        for (DataType dataType : DataType.values()) {
            if (dataType.type == name) {
                return dataType;
            }
        }
        return null;
    }
}
