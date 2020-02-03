package com.thunder_cut.server.attachment;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Vector;

public class ClientAttachment {

    private AsynchronousSocketChannel client;
    private ByteBuffer buffer;
    private Vector<ClientAttachment> clientGroup;
    private boolean readyToWrite;

    public boolean isReadyToWrite() {
        return readyToWrite;
    }

    public void setReadyToWrite(boolean readyToWrite) {
        this.readyToWrite = readyToWrite;
    }

    public Vector<ClientAttachment> getClientGroup() {
        return clientGroup;
    }

    public void setClientGroup(Vector<ClientAttachment> clientGroup) {
        this.clientGroup = clientGroup;
    }

    public AsynchronousSocketChannel getClient() {
        return client;
    }

    public void setClient(AsynchronousSocketChannel client) {
        this.client = client;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }
}
