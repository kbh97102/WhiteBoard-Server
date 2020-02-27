/*
 * DataGenerator.java
 * Author : Arakene
 * Created Date : 2020-02-24
 */
package com.thunder_cut.feature;

import com.thunder_cut.data.DataType;
import com.thunder_cut.data.ReceivedData;

import java.nio.ByteBuffer;

public class DataGenerator {

    private ReceivedData receivedData;

    public DataGenerator(ReceivedData receivedData) {
        this.receivedData = receivedData;
    }

    public ByteBuffer generate() {
        if (receivedData.getDataType() == DataType.IMG) {
            return makeImageData();
        } else {
            return makeMessageData();
        }
    }

    private ByteBuffer makeImageData() {
        ByteBuffer sendingData = ByteBuffer.allocate(receivedData.getBuffer().array().length + 10);
        sendingData.putChar(receivedData.getDataType().type);
        sendingData.putInt(receivedData.getSrcID());
        sendingData.putInt(receivedData.getBuffer().array().length);
        sendingData.put(receivedData.getBuffer());

        sendingData.flip();

        return sendingData;
    }

    /**
     * Make message data with client's name
     * @return message data
     */
    private ByteBuffer makeMessageData() {
        String nickName = receivedData.getName().concat("::");
        ByteBuffer sendingData = ByteBuffer.allocate(receivedData.getBuffer().array().length + 10 + nickName.getBytes().length);
        sendingData.putChar(receivedData.getDataType().type);
        sendingData.putInt(receivedData.getSrcID());
        sendingData.putInt(receivedData.getBuffer().array().length + nickName.getBytes().length);
        sendingData.put(nickName.getBytes());
        sendingData.put(receivedData.getBuffer());

        sendingData.flip();

        return sendingData;
    }
}
