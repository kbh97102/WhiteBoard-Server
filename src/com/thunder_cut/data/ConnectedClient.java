/*
 * ConnectedClient.java
 * Author : Arakene
 * Created Date : 2020-02-24
 */
package com.thunder_cut.data;

import com.thunder_cut.connection.Requests;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConnectedClient {

    private String name;
    private SocketChannel client;
    private Requests requests;
    private List<Integer> ignoreList;
    private boolean op;

    public ConnectedClient(SocketChannel client, Requests callBack) {
        this.client = client;
        this.requests = callBack;
        ignoreList = Collections.synchronizedList(new ArrayList<>());
        op = false;
        name = Integer.toString(this.hashCode());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SocketChannel getClient() {
        return client;
    }

    public List<Integer> getIgnoreList() {
        return ignoreList;
    }

    public boolean isOp() {
        return op;
    }

    public void setOP(boolean op) {
        this.op = op;
    }

    public void startReceiveFromClientToServer() {
        new Thread(this::receive).start();
    }

    private void receive() {
        while (true) {
            ByteBuffer header = ByteBuffer.allocate(6);
            ByteBuffer data;
            try {
                int result = client.read(header);
                if (result != 6) {
                    break;
                }
            } catch (IOException e) {
                break;
            }

            header.flip();
            char type = header.getChar();
            int size = header.getInt();

            data = ByteBuffer.allocate(size);
            while (data.hasRemaining()) {
                try {
                    client.read(data);
                } catch (IOException e) {
                    break;
                }
            }
            data.flip();
            requests.requestWriteToClient(new ReceivedData(DataType.valueOf(type), data, requests.getID(this), name));
        }
        requests.disconnect(this);
    }
}
