package com.thunder_cut.connection;

import com.thunder_cut.data.ConnectedClient;
import com.thunder_cut.data.ReceivedData;

/**
 * Request work to server
 * requestWriteToClient : generate data for write by given receivedData and then write
 * disconnect : disconnect given client and finish all work about client
 * getID : return client's index in clients <- list
 * getClient : return client in clients
 */
public interface Requests {

    void requestWriteToClient(ReceivedData receivedData);

    void disconnect(ConnectedClient target);

    int getID(ConnectedClient client);

    int getID(String name);

    ConnectedClient getClient(int srcID);
}
