/*
 * MainClass.java
 * Author: Seokjin Yoon
 * Created Date: 2020-01-10
 */

package com.thunder_cut;


import com.thunder_cut.connection.Server;

public class MainClass {

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        Server server = new Server(null, 3001);
        new Thread(server).start();
    }
}
