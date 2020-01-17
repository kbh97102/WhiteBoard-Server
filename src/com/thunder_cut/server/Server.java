/*
 * Server.java
 * Author : Arakene
 * Created Date : 2020-01-15
 */
package com.thunder_cut.server;

import com.thunder_cut.server.handler.ServerAcceptHandler;
import com.thunder_cut.server.handler.ServerReadHandler;
import com.thunder_cut.server.handler.ServerWriteHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Vector;

public class Server implements Runnable {

    private final static int PORT = 3001;
    private AsynchronousServerSocketChannel serverSocket;
    private Vector<Attachment> clientGroup = new Vector<>();
    private Charset charset = StandardCharsets.UTF_8;
    private ByteBuffer buffer;

    /**
     * All IP, Default Port
     */
    public Server() {
        try {
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * All IP, Custom Port
     *
     * @param port Custom Port is that user want to connect
     */
    public Server(int port) {
        try {
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Specific IP, Default Port
     *
     * @param ip Specific IP is that user want to connect
     */
    public Server(String ip) {
        try {
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(ip, PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param ip   Custom IP
     * @param port Custom Port
     */
    public Server(String ip, int port) {
        try {
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(ip, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * When this method is called Server Will be waiting for connect from client with ServerAcceptHandler
     * and Start reading with ServerReadHandler
     */
    public void startAccept() {
        Attachment ServerInfo = new Attachment();
        ServerInfo.setServer(serverSocket);
        ServerInfo.setClientGroup(clientGroup);
        serverSocket.accept(ServerInfo, new ServerAcceptHandler(this::readFromClient).getHandler());
    }

    /**
     * If Client is not readMode start Reading
     */
    public void readFromClient() {
        for (Attachment clientInfo : clientGroup) {
            if (!clientInfo.isReadMode()) {
                clientInfo.setReadMode(true);
                clientInfo.getClient().read(clientInfo.getBuffer(), clientInfo, new ServerReadHandler().getReadHandler());
            }
        }
    }

    /**
     * Send String Message to All client in ClientGroup
     *
     * @param message String Message
     */
    public void writeToAllClients(String message) {
        buffer = charset.encode(message);
        for (Attachment clientInfo : clientGroup) {
            clientInfo.getClient().write(buffer, buffer, new ServerWriteHandler().getWriteHandler());
        }
        buffer.clear();
    }

    /**
     * Convert Image Data to ByteBuffer and Send all client
     *
     * @param imageLabel JLabel have a image for sending
     */
    public void writeToAllClients(JLabel imageLabel) {
        try {
            ImageIcon imageIcon = (ImageIcon) imageLabel.getIcon();
            Image image = imageIcon.getImage();
            BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.drawImage(image, null, null);
            graphics.dispose();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", output);
            output.flush();
            ByteBuffer buffer = ByteBuffer.wrap(output.toByteArray());

            for (Attachment client : clientGroup) {
                client.getClient().write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pick specific client and send String message
     *
     * @param message     String message
     * @param clientIndex Sending Target
     */
    public void writeToSpecificClient(String message, int clientIndex) {
        buffer = charset.encode(message);
        clientGroup.get(clientIndex).getClient().write(buffer, buffer, new ServerWriteHandler().getWriteHandler());
        buffer.clear();
    }

    /**
     * Convert Image Data to ByteBuffer and Send specific client
     *
     * @param imageLabel  JLabel have a image for sending
     * @param clientIndex Receiving target
     */
    public void writeToSpecificClient(JLabel imageLabel, int clientIndex) {
        try {
            ImageIcon imageIcon = (ImageIcon) imageLabel.getIcon();
            Image image = imageIcon.getImage();
            BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.drawImage(image, null, null);
            graphics.dispose();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", output);
            output.flush();
            ByteBuffer buffer = ByteBuffer.wrap(output.toByteArray());

            clientGroup.get(clientIndex).getClient().write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * If you use GUI remove Implement Runnable
     * <p>
     * If user input Quit writing will shutdown
     */
    @Override
    public void run() {
        startAccept();

        String input;
        Scanner scanner = new Scanner(System.in);
        while (!(input = scanner.nextLine()).equals("Quit")) {
            writeToAllClients(input);
        }

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
