/*
 * Server.java
 * Author : Arakene
 * Created Date : 2020-01-15
 */
package com.server;

import com.server.handler.ServerAcceptHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.Vector;

public class Server {

    private static String IP = "127.0.0.1";
    private static int PORT = 3000;
    private AsynchronousServerSocketChannel serverSocket;
    private Vector<Attachment> clientGroup = new Vector<>();

    public Server() {

        try {
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(IP, PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startAccept(){
        Attachment ServerInfo = new Attachment();
        ServerInfo.setServer(serverSocket);
        ServerInfo.setClients(clientGroup);
        serverSocket.accept(ServerInfo, new ServerAcceptHandler().getHandler());
    }
}
