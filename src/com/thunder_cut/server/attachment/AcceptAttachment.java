package com.thunder_cut.server.attachment;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.Vector;

public class AcceptAttachment {

    private AsynchronousServerSocketChannel server;
    private Vector<ClientAttachment> clientGroup;

    public AsynchronousServerSocketChannel getServer() {
        return server;
    }

    public void setServer(AsynchronousServerSocketChannel server) {
        this.server = server;
    }

    public Vector<ClientAttachment> getClientGroup() {
        return clientGroup;
    }

    public void setClientGroup(Vector<ClientAttachment> clientGroup) {
        this.clientGroup = clientGroup;
    }
}
