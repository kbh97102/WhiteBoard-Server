/*
 * Server.java
 * Author : Arakene
 * Created Date : 2020-01-15
 */
package com.server;

import com.server.handler.ServerAcceptHandler;
import com.server.handler.ServerReadHandler;
import com.server.handler.ServerWriteHandler;

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
    private Charset charset = StandardCharsets.UTF_8;
    private ByteBuffer buffer;

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
        ServerInfo.setClientGroup(clientGroup);
        serverSocket.accept(ServerInfo, new ServerAcceptHandler(this::readFromClient).getHandler());
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
        buffer = charset.encode(message);
        for(Attachment clientInfo : clientGroup){
            clientInfo.getClient().write(buffer, buffer,new ServerWriteHandler().getWriteHandler());
        }
        buffer.clear();
    }

    public void writeToSpecificClient(String message, int clientIndex){
        buffer = charset.encode(message);
        clientGroup.get(clientIndex).getClient().write(buffer, buffer, new ServerWriteHandler().getWriteHandler());
        buffer.clear();
    }
}
