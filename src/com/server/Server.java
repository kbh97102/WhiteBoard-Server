/*
 * Server.java
 * Author : Arakene
 * Created Date : 2020-01-15
 */
package com.server;

import com.server.handler.ServerAcceptHandler;
import com.server.handler.ServerReadHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

    public void readFromClient(){
        for(Attachment clientInfo : clientGroup){
            if(!clientInfo.isReadMode()){
                clientInfo.setReadMode(true);
                clientInfo.getClient().read(clientInfo.getBuffer(),clientInfo, new ServerReadHandler().getReadHandler());
            }
        }
    }

    public void writeToAllClients(String message){
        Charset charset = StandardCharsets.UTF_8;
        ByteBuffer buffer = charset.encode(message);
        for(Attachment clientInfo : clientGroup){
            clientInfo.getClient().write(buffer);
        }
    }
}
