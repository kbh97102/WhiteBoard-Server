/*
 * ServerWriteHandler.java
 * Author : Arakene
 * Created Date : 2020-01-15
 */
package com.thunder_cut.socket.handler;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

/**
 * This class will work After write method
 * Clear ByteBuffer for next write method
 */

public class ServerWriteHandler {

    private CompletionHandler<Integer, ByteBuffer> writeHandler;

    public ServerWriteHandler() {
        writeHandler = new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                attachment.clear();
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                System.out.println("Server Write Error");
            }
        };
    }

    public CompletionHandler<Integer, ByteBuffer> getWriteHandler() {
        return writeHandler;
    }
}
