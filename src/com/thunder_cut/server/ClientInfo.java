/*
 * ClientInfo.java
 * Author : Arakene
 * Created Date : 2020-02-04
 */
package com.thunder_cut.server;

import com.thunder_cut.server.data.DataType;
import com.thunder_cut.server.data.ReceivedData;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;

/**
 * This class has information, read and write about connected client
 */
public class ClientInfo {

    interface DisconnectCallBack {
        void disconnect(ClientInfo client);
    }

    interface CallClientMap {
        Map<ClientInfo, List<ClientInfo>> getMap();
    }

    public int ID;
    private String name;
    private SocketChannel client;
    private DisconnectCallBack disconnectCallBack;
    private Process processing;
    private boolean op;

    public boolean isOp() {
        return op;
    }

    public void setOp(boolean op) {
        this.op = op;
    }

    private CallClientMap callMap;

    public ClientInfo(int ID) {
        this.ID = ID;
    }

    public SocketChannel getClient() {
        return client;
    }

    public void setClient(SocketChannel client) {
        this.client = client;
    }

    public void read() {
        new Thread(this::reading).start();
    }

    public void setCallBack(DisconnectCallBack disconnect, CallClientMap callMap) {
        this.disconnectCallBack = disconnect;
        this.callMap = callMap;

        processing = new Process(disconnect::disconnect);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * 1. Allocate ByteBuffer with header size and read header
     * <p>
     * 2. Figure out data type, data size and allocate ByteBuffer with data size
     * <p>
     * 3. Receive data until buffer has no space
     * <p>
     * 4. Give ID, data type and data(no header) to write
     */
    private void reading() {
        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(6);
            try {
                client.read(buffer);
            } catch (IOException e) {
                break;
            }
            buffer.flip();

            char type = 'I';
            int size = 0;
            try {
                type = buffer.getChar();
                size = buffer.getInt();
            } catch (BufferUnderflowException e1) {
                break;
            }

            buffer = ByteBuffer.allocate(size);

            while (buffer.hasRemaining()) {
                try {
                    client.read(buffer);
                } catch (IOException e) {
                    break;
                }
            }

            buffer.flip();
            //양쪽만 같게끔하면 문제 없음
//            buffer.order(ByteOrder.BIG_ENDIAN);
            ReceivedData receivedData = new ReceivedData(this, DataType.valueOf(type), buffer);
//            System.out.println(new String(buffer.array()));
            processing.getProcessMap().get(DataType.valueOf(type)).accept(receivedData, callMap.getMap());
        }
        disconnectCallBack.disconnect(this);
    }

}