/*
 * Server.java
 * Author : Arakene
 * Created Date : 2020-02-24
 */
package com.thunder_cut.connection;


import com.thunder_cut.data.ConnectedClient;
import com.thunder_cut.data.DataType;
import com.thunder_cut.data.ReceivedData;
import com.thunder_cut.feature.CommandExecutor;
import com.thunder_cut.feature.DataGenerator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Server implements Runnable, Requests {

    private final List<ConnectedClient> clients;
    private ServerSocketChannel server;

    public Server(String ip, int port) {
        try {
            server = ServerSocketChannel.open();
            server.bind(ip == null ? new InetSocketAddress(port) : new InetSocketAddress(ip, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        clients = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Start accept and start reading data from client
     * This method keep alive until program is dead
     */
    @Override
    public void run() {
        while (true) {
            try {
                SocketChannel client = server.accept();
                InetSocketAddress address = (InetSocketAddress) client.getRemoteAddress();
                ConnectedClient connectedClient = new ConnectedClient(client, this);
                if (clients.size() == 0) {
                    connectedClient.setOP(true);
                }
                clients.add(connectedClient);
                connectedClient.startReceiveFromClientToServer();

                String connectMessage = address.getHostName() + " " + address.getPort() + " is connected";
                writeServerMessage(connectMessage, connectedClient.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getID(ConnectedClient client) {
        return clients.indexOf(client);
    }

    @Override
    public int getID(String name) {
        for (int index = 0; index < clients.size(); index++) {
            if (clients.get(index).getName().equals(name)) {
                return index;
            }
        }
        return -1;
    }

    @Override
    public ConnectedClient getClient(int srcID) {
        if (srcID >= clients.size()) {
            return null;
        }
        return clients.get(srcID);
    }

    @Override
    public void disconnect(ConnectedClient target) {
        try {
            if (target.getClient().isConnected()) {
                InetSocketAddress address = (InetSocketAddress)target.getClient().getRemoteAddress();
                target.getClient().close();
                clients.remove(target);
                if (clients.size() >= 1) {
                    writeServerMessage(address.getHostName().concat(" is disconnected"), target.getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (ConnectedClient client : clients) {
            client.getIgnoreList().clear();
        }
    }

    private void writeServerMessage(String message, String name) {
        ByteBuffer buffer = ByteBuffer.wrap((message.concat("\n")).getBytes());
        ReceivedData messageData = new ReceivedData(DataType.MSG, buffer, 0, name);
        requestWriteToClient(messageData);
    }

    @Override
    public void requestWriteToClient(ReceivedData receivedData) {
        DataGenerator dataGenerator = new DataGenerator(receivedData);
        if (receivedData.getDataType() != DataType.CMD) {
            broadcast(receivedData.getSrcID(), dataGenerator.generate());
        } else {
            new CommandExecutor(receivedData, this).execute();
        }
    }

    /**
     * Write to all client without ignored client
     *
     * @param srcID client's index who received data
     * @param data  generated data
     */
    private synchronized void broadcast(int srcID, ByteBuffer data) {
        ConnectedClient src = clients.get(srcID);
        for (int index = 0; index < clients.size(); index++) {
            if (!src.getIgnoreList().contains(index)) {
                unicast(index, data);
                data.flip();
            }
        }
    }

    private synchronized void unicast(int destIndex, ByteBuffer data) {
        try {
            clients.get(destIndex).getClient().write(data);
        } catch (IOException e) {
            disconnect(clients.get(destIndex));
        }
    }
}