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

//클라간의 연결/ 연결해제만
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

    @Override
    public void run() {
        while (true) {
            try {
                SocketChannel client = server.accept();
                InetSocketAddress address = (InetSocketAddress) client.getRemoteAddress();
                System.out.println(address.getHostName() + " " + address.getPort() + " is connected");
                ConnectedClient connectedClient = new ConnectedClient(client, this);
                if (clients.size() == 0) {
                    connectedClient.setOP(true);
                }
                clients.add(connectedClient);
                connectedClient.startReceiveFromClientToServer();
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
                target.getClient().close();
                clients.remove(target);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (ConnectedClient client : clients) {
            client.getIgnoreList().clear();
        }
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
//함수 이름을 정하기 힘들다 == 기능을 분리해야된다
//데이터를 전달받아 만들기 + 보내기를 같이 하는 중
//generator, send 를 나눠야 됨 어떻게?
//Server Class안에서 또 다른 클래스를 생성하면 종속성이 생김
//그렇다고 Server를 생성하는 쪽에서 넘겨주면 또 구조가 맘에 안듬 (참고 Dependency Injection)

    /*
    하나하나 지시하지 말고 요청해라.

예를들어, 판사가 증인에게 1) 목격했던 장면을 떠올리고, 2) 떠오르는 시간을 순서대로 구성하고, 3) 말로 간결하게 표현해라 라고 요청하지 않는다. 그냥 "증언하라" 라고 요청한다.
마찬가지로 객체의 설계단계에서도 책임이 있는 객체에 요청만 하도록 설계한다.
     */
