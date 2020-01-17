/*
 * ServerAcceptHandler.java
 * Author : Arakene
 * Created Date : 2020-01-15
 */
package com.thunder_cut.server.handler;

import com.thunder_cut.server.Attachment;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ServerAcceptHandler {

    private CompletionHandler<AsynchronousSocketChannel, Attachment> acceptHandler;


    public ServerAcceptHandler(Runnable readHandler){
        acceptHandler = new CompletionHandler<AsynchronousSocketChannel, Attachment>() {
            /**
             * If Accept is success completed will be start
             * Create new ClientInfo , Start reading from this client, and Save ClientGroup
             * Accept method keep running with this acceptHandler
             * @param result accepted socket
             * @param attachment Server Information
             */
            @Override
            public void completed(AsynchronousSocketChannel result, Attachment attachment) {
                try {
                    System.out.println(result.getRemoteAddress()+" is connected");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Attachment clientInfo = new Attachment();
                clientInfo.setServer(attachment.getServer());
                clientInfo.setClient(result);
                clientInfo.setBuffer(ByteBuffer.allocate(10000000));
                clientInfo.setReadMode(false);

                attachment.getClientGroup().add(clientInfo);

                readHandler.run();

                attachment.getServer().accept(attachment,this);
            }

            @Override
            public void failed(Throwable exc, Attachment attachment) {
                System.out.println("Server Accept Error");
            }
        };
    }

    public CompletionHandler<AsynchronousSocketChannel, Attachment> getHandler(){
        return acceptHandler;
    }

}
