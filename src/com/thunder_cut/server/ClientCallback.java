/*
 * ClientCallback.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-14
 */

package com.thunder_cut.server;

import com.thunder_cut.server.data.DataType;

public interface ClientCallback {
    void received(ClientInfo client, DataType type, byte[] data);

    void disconnected(ClientInfo client);
}
