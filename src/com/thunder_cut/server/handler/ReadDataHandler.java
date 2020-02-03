package com.thunder_cut.server.handler;


import com.thunder_cut.server.attachment.ClientAttachment;

import java.nio.channels.CompletionHandler;

public class ReadDataHandler {

    private CompletionHandler<Integer, ClientAttachment> readDataHandler;

    public ReadDataHandler() {

        readDataHandler = new CompletionHandler<Integer, ClientAttachment>() {
            @Override
            public void completed(Integer result, ClientAttachment attachment) {
                System.out.println("Read Data : " + attachment.getBuffer().toString());
                attachment.getBuffer().flip();
                attachment.setReadyToWrite(true);
            }

            @Override
            public void failed(Throwable exc, ClientAttachment attachment) {

            }
        };
    }

    public CompletionHandler<Integer, ClientAttachment> getReadDataHandler() {
        return readDataHandler;
    }
}
