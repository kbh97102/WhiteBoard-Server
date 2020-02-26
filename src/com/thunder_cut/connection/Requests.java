package com.thunder_cut.connection;

import com.thunder_cut.data.ConnectedClient;
import com.thunder_cut.data.ReceivedData;

public interface Requests {

    void requestWriteToClient(ReceivedData receivedData);

    void disconnect(ConnectedClient target);

    int getID(ConnectedClient client);

    int getID(String name);

    ConnectedClient getClient(int srcID);
}
