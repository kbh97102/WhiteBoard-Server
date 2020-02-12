/*
 * ClientInformation.java
 * Author : Arakene
 * Created Date : 2020-02-04
 */
package com.thunder_cut.server;

import java.io.IOException;
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

    private void reading() {
        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(6);
            try {
                client.read(buffer);
            } catch (IOException e) {
                break;
            }
            buffer.flip();

            char type = buffer.getChar();
            int size = buffer.getInt();
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
}