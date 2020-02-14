/*
 * ClientInformation.java
 * Author : Arakene
 * Created Date : 2020-02-04
 */
package com.thunder_cut.server;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * This class has information, read and write about connected client
 */
public class ClientInformation {

    interface WriteCallBack {
        void write(int srcID, char type, ByteBuffer buffer);
    }

    public int ID;

    private SocketChannel client;
    private WriteCallBack writeToAll;

    public ClientInformation(int ID) {
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

    public void setSending(WriteCallBack callBack) {
        writeToAll = callBack;
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
                close();
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
            writeToAll.write(ID, type, buffer);
        }
    }

    private void close() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Error! Please Connect Again");
    }
}