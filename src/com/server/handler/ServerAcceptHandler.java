/*
 * ServerAcceptHandler.java
 * Author : Arakene
 * Created Date : 2020-01-15
 */
package com.server.handler;

import com.server.Attachment;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ServerAcceptHandler {

    private CompletionHandler<AsynchronousSocketChannel, Attachment> acceptHandler;
    public ServerAcceptHandler(){
        acceptHandler = new CompletionHandler<AsynchronousSocketChannel, Attachment>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Attachment attachment) {
                Attachment clientInfo = new Attachment();
                clientInfo.setServer(attachment.getServer());
                clientInfo.setClient(result);
                clientInfo.setBuffer(ByteBuffer.allocate(10000000));
                clientInfo.setReadMode(false);

                attachment.getClients().add(clientInfo);

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
