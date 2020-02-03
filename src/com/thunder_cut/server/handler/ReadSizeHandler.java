package com.thunder_cut.server.handler;


import com.thunder_cut.server.attachment.ClientAttachment;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public class ReadSizeHandler {

    private CompletionHandler<Integer, ClientAttachment> readSizeHandler;

    public ReadSizeHandler() {

        readSizeHandler = new CompletionHandler<Integer, ClientAttachment>() {
            @Override
            public void completed(Integer result, ClientAttachment attachment) {
                attachment.getBuffer().flip();
                int bufferSize = attachment.getBuffer().getInt();
                try {
                    attachment.setBuffer(ByteBuffer.allocate(bufferSize));
                } catch (IllegalArgumentException e1) {
                    System.out.println("Error");
                    attachment.getBuffer().clear();
                    attachment.getClient().read(attachment.getBuffer());
                    System.out.println(attachment.getBuffer().toString());
                    bufferSize = attachment.getBuffer().getInt();
                    attachment.setBuffer(ByteBuffer.allocate(bufferSize));
                }


                attachment.getClient().read(attachment.getBuffer(), attachment, new ReadDataHandler().getReadDataHandler());
            }

            @Override
            public void failed(Throwable exc, ClientAttachment attachment) {

            }
        };
    }

    public CompletionHandler<Integer, ClientAttachment> getReadSizeHandler() {
        return readSizeHandler;
    }
}
