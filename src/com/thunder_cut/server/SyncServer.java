/*
 * SyncServer.java
 * Author : Arakene
 * Created Date : 2020-02-04
 */

package com.thunder_cut.server;

import com.thunder_cut.server.data.DataType;
import com.thunder_cut.server.data.ReceivedData;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * This class is main class about server
 * If program start, generate server and bind given data
 * Accept method keep running until program is shutdown
 */
public class SyncServer implements ClientCallback, Runnable {
    private static final int PORT = 3001;
    private ServerSocketChannel server;
    private final List<ClientInfo> clientGroup;
    private Process process;
    private final Map<ClientInfo, List<ClientInfo>> clientMap;

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
        clientGroup = Collections.synchronizedList(new ArrayList<>());
        process = new Process(this::disconnected);
        clientMap = new HashMap<>();
    }

    /**
     * Detect client connection and Generate ClientInfo with connected client
     * After Generating Add clientGroup and start readingData from client
     */
    @Override
    public void run() {
        while (true) {
            try {
                SocketChannel client = server.accept();
                System.out.println(client.getRemoteAddress() + " is connected.");
                ClientInfo clientInfo = new ClientInfo(client, this, clientGroup.size());
                clientInfo.setOp(false);
                synchronized (clientGroup) {
                    clientGroup.add(clientInfo);
                }
                synchronized (clientMap) {
                    for (ClientInfo information : clientMap.keySet()) {
                        clientMap.get(information).add(clientInfo);
                    }
                    List<ClientInfo> list = Collections.synchronizedList(new ArrayList<>());
                    list.addAll(clientGroup);
                    clientMap.put(clientInfo, list);
                }
                clientInfo.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void received(ReceivedData data) {
        process.getProcessMap().get(DataType.valueOf(data.getDataType().type)).accept(data, clientMap);
    }

    //TODO Resolve Bug (Read Git Issue), When disconnect change client
    /**
     * Remove disconnected client in clientGroup
     * After that change client's ID by ascending sort
     *
     * @param client disconnected client
     */
    @Override
    public void disconnected(ClientInfo client) {
        try {
            System.out.println(client.getClient().getRemoteAddress() + " is disconnected.");
            client.getClient().close();
            synchronized (clientGroup) {
                clientGroup.remove(client);
            }
            synchronized (clientMap) {
                clientMap.remove(client);
                for (ClientInfo information : clientMap.keySet()) {
                    clientMap.get(information).remove(client);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectionClear(){
        for(ClientInfo key : clientMap.keySet()){
            for(ClientInfo client : clientMap.get(key)){
                try {
                    client.getClient().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            clientMap.remove(key);
        }
        clientGroup.clear();
    }
}
