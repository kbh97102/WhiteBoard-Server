package com.thunder_cut.server.handler;

import com.thunder_cut.server.attachment.AcceptAttachment;
import com.thunder_cut.server.attachment.ClientAttachment;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptHandler {

    private CompletionHandler<AsynchronousSocketChannel, AcceptAttachment> acceptHandler;

    public AcceptHandler() {

        acceptHandler = new CompletionHandler<AsynchronousSocketChannel, AcceptAttachment>() {
            @Override
            public void completed(AsynchronousSocketChannel result, AcceptAttachment attachment) {
                try {
                    System.out.println(result.getRemoteAddress() + " is connect");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ClientAttachment clientInfo = new ClientAttachment();
                clientInfo.setClient(result);
                clientInfo.setBuffer(ByteBuffer.allocate(1024));

                attachment.getClientGroup().add(clientInfo);
                clientInfo.setClientGroup(attachment.getClientGroup());

                attachment.getServer().accept(attachment, this);

                clientInfo.setReadyToWrite(false);
                clientInfo.getClient().read(clientInfo.getBuffer(), clientInfo, new ReadSizeHandler().getReadSizeHandler());
            }

            @Override
            public void failed(Throwable exc, AcceptAttachment attachment) {

            }
        };
    }

    public CompletionHandler<AsynchronousSocketChannel, AcceptAttachment> getAcceptHandler() {
        return acceptHandler;
    }
}
