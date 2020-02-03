/*
 * MainClass.java
 * Author: Seokjin Yoon
 * Created Date: 2020-01-10
 */

package com.thunder_cut;

import com.thunder_cut.server.Server;

public class MainClass {
    public static void main(String[] args) {
        Server server;
        if (args.length == 0) {
            server = new Server();
        } else if (args.length == 1) {
            int port = Integer.parseInt(args[0]);
            server = new Server(port);
        } else {
            String ip = args[0];
            int port = Integer.parseInt(args[1]);
            server = new Server(ip, port);
        }
        server.run();
    }
}
