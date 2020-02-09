/*
 * SyncServer.java
 * Author : Arakene
 * Created Date : 2020-02-04
 */

package com.thunder_cut.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncServer {

    private static final int PORT = 3001;
    private ServerSocketChannel server;
    private ExecutorService executorService;
    private final List<ClientInformation> clientGroup;

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
            initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientGroup = Collections.synchronizedList(new ArrayList<>());
    }

    private void initialize() {
        executorService = Executors.newFixedThreadPool(10);
    }

    public void run() {
        executorService.submit(this::accept);
    }

    private void accept() {
        while (true) {
            try {
                SocketChannel client = server.accept();
                System.out.println(client.getRemoteAddress() + " is connect");
                ClientInformation clientInformation = new ClientInformation(clientGroup, clientGroup.size() + 1);
                clientInformation.setClient(client);

                synchronized (clientGroup) {
                    clientGroup.add(clientInformation);
                }

                clientInformation.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
