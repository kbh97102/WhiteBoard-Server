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
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * This class has information, read and write about connected client
 */
public class ClientInfo {

    interface AddCallBack {
        void add(ReceivedData result);
    }
    interface DisconnectCallBack{
        void disconnect(ClientInfo client);
    }

    public int ID;
    private String name;
    private SocketChannel client;
    private AddCallBack addCallBack;
    private DisconnectCallBack disconnectCallBack;

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

    public void setCallBack(AddCallBack add, DisconnectCallBack disconnect) {
        this.addCallBack = add;
        this.disconnectCallBack = disconnect;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
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

            // "/"가 메세지의 시작이면 포함되어있으면 커맨드로 처리
            buffer = ByteBuffer.allocate(size);

            while (buffer.hasRemaining()) {
                try {
                    client.read(buffer);
                } catch (IOException e) {
                    break;
                }
            }

            buffer.flip();
//            System.out.println("Received : "+buffer.toString());
            System.out.println(DataType.valueOf(type));            ReceivedData receivedData = new ReceivedData(this, DataType.valueOf(type), buffer);
            addCallBack.add(receivedData);
        }
        disconnectCallBack.disconnect(this);
    }

}