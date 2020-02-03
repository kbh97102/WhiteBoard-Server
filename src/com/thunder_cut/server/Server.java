package com.thunder_cut.server;

import com.thunder_cut.server.attachment.AcceptAttachment;
import com.thunder_cut.server.attachment.ClientAttachment;
import com.thunder_cut.server.handler.AcceptHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.Vector;

public class Server {
    private static final int PORT = 3001;

    private AsynchronousServerSocketChannel server;
    private Vector<ClientAttachment> clientGroup = new Vector<>();

    /**
     * All IP, Default Port
     */
    public Server() {
        this(null, PORT);
    }

    /**
     * All IP, Custom Port
     *
     * @param port Custom Port is that user want to connect
     */
    public Server(int port) {
        this(null, port);
    }

    /**
     * Specific IP, Default Port
     *
     * @param ip Specific IP is that user want to connect
     */
    public Server(String ip) {
        this(ip, PORT);
    }

    /**
     * @param ip   Custom IP
     * @param port Custom Port
     */
    public Server(String ip, int port) {
        try {
            server = AsynchronousServerSocketChannel.open();
            server.bind(ip == null ? new InetSocketAddress(port) : new InetSocketAddress(ip, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void accept() {
        AcceptAttachment acceptInfo = new AcceptAttachment();
        acceptInfo.setServer(server);
        acceptInfo.setClientGroup(clientGroup);
        server.accept(acceptInfo, new AcceptHandler().getAcceptHandler());
    }

}

