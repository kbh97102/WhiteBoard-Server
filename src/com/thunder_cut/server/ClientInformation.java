/*
 * ClientInformation.java
 * Author : Arakene
 * Created Date : 2020-02-04
 */
package com.thunder_cut.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class has information, read and write about connected client
 */
public class ClientInformation {

    private static final Map<Character, DataType> dataTypeMap;

    private SocketChannel client;
    private final List<ClientInformation> clientGroup;
    public final int ID;

    static {
        dataTypeMap = new HashMap<>();
        for (DataType dataTypeEnum : DataType.values()) {
            dataTypeMap.put(dataTypeEnum.type, dataTypeEnum);
        }
    }

    public ClientInformation(List<ClientInformation> clientGroup, int ID) {
        this.clientGroup = clientGroup;
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

    private void reading() {
        while (true) {
            try {
                //read header
                ByteBuffer buffer = ByteBuffer.allocate(6);
                client.read(buffer);
                buffer.flip();

                char type = buffer.getChar();
                int size = buffer.getInt();

                buffer = ByteBuffer.allocate(size);

                //read data
                client.read(buffer);
                buffer.flip();

                //Generate Data and Send to All
                synchronized (clientGroup) {
                    for (ClientInformation destination : clientGroup) {
                        SendingData sendingData = new SendingData(ID, destination.ID, dataTypeMap.get(type), buffer.array());
                        int written = destination.getClient().write(sendingData.toByteBuffer());
                        //If writing failed, remove this client
                        if (written == 0) {
                            System.out.println(destination.getClient().getRemoteAddress() + " is disconnected");
                            destination.getClient().close();
                            clientGroup.remove(destination);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}


