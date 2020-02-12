/*
 * SyncServer.java
 * Author : Arakene
 * Created Date : 2020-02-04
 */

package com.thunder_cut.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is main class about server
 * If program start, generate server and bind given data
 * Accept method keep running until program is shutdown
 */
public class SyncServer {

    private static final Map<Character, DataType> dataTypeMap;
    private static final int PORT = 3001;
    private ServerSocketChannel server;
    private ExecutorService executorService;
    private final List<ClientInformation> clientGroup;

    static {
        dataTypeMap = new HashMap<>();
        for (DataType dataTypeEnum : DataType.values()) {
            dataTypeMap.put(dataTypeEnum.type, dataTypeEnum);
        }
    }

    /**
     * All IP, Default Port
     */
    public SyncServer() {
        this(null, PORT);
    }

    /**
     * All IP, Custom Port
     *
     * @param port Custom Port is that user want to connect
     */
    public SyncServer(int port) {
        this(null, port);
    }

    /**
     * Specific IP, Default Port
     *
     * @param ip Specific IP is that user want to connect
     */
    public SyncServer(String ip) {
        this(ip, PORT);
    }

    /**
     * @param ip   Custom IP
     * @param port Custom Port
     */
    public SyncServer(String ip, int port) {
        try {
            server = ServerSocketChannel.open();
            server.bind(ip == null ? new InetSocketAddress(port) : new InetSocketAddress(ip, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        initialize();
        clientGroup = Collections.synchronizedList(new ArrayList<>());
    }

    private void initialize() {
        executorService = Executors.newFixedThreadPool(10);
    }

    public void run() {
        executorService.submit(this::accept);
    }

    private void accept() {
        ClientInformation.WriteCallBack sending = this::identifyWriteMode;
        while (true) {
            try {
                SocketChannel client = server.accept();
                System.out.println(client.getRemoteAddress() + " is connect");
                ClientInformation clientInformation = new ClientInformation(clientGroup.size());
                clientInformation.setClient(client);
                clientInformation.setSending(sending);
                synchronized (clientGroup) {
                    clientGroup.add(clientInformation);
                }
                clientInformation.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void identifyWriteMode(int srcID, char type, ByteBuffer buffer) {
        if (dataTypeMap.get(type) == DataType.CMD) {
            writeToSrc(srcID, type, buffer);
        } else {
            writeToAll(srcID, type, buffer);
        }
    }

    private void writeToAll(int srcID, char type, ByteBuffer buffer) {
        synchronized (clientGroup) {
            for (ClientInformation destination : clientGroup) {
                SendingData sendingData = new SendingData(srcID, destination.ID, dataTypeMap.get(type), buffer.array());
                try {
                    destination.getClient().write(sendingData.toByteBuffer());
                } catch (IOException e) {
                    removeClient(destination);
                    return;
                }
            }
        }
    }

    private void writeToSrc(int srcID, char type, ByteBuffer buffer) {
        synchronized (clientGroup) {
            for (ClientInformation destination : clientGroup) {
                if (destination.ID == srcID) {
                    SendingData sendingData = new SendingData(srcID, destination.ID, dataTypeMap.get(type), buffer.array());
                    try {
                        destination.getClient().write(sendingData.toByteBuffer());
                    } catch (IOException e) {
                        removeClient(destination);
                        return;
                    }
                    break;
                }
            }
        }
    }

    private void removeClient(ClientInformation removeTarget) {
        System.out.println("Client " + removeTarget.ID + " is disconnected");
        clientGroup.remove(removeTarget);
        synchronized (clientGroup) {
            for (int i = 0; i < clientGroup.size(); i++) {
                clientGroup.get(i).ID = i;
                System.out.println(clientGroup.get(i).ID + " Size : " + clientGroup.size());
            }
        }
    }

}
