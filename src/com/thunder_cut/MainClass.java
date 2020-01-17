/*
 * MainClass.java
 * Author: Seokjin Yoon
 * Created Date: 2020-01-10
 */

package com.thunder_cut;


import com.thunder_cut.server.Server;

public class MainClass {
    public static void main(String[] args) {
        Server server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();
    }
}
