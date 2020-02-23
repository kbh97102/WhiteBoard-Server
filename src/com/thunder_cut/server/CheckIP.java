/*
 * CheckIP.java
 * Author : Arakene
 * Created Date : 2020-02-22
 */
package com.thunder_cut.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CheckIP {

    private static final String FILEPATH = "BlackList.txt";
    private List<String> blackList;
    private List<InetSocketAddress> blackListBuffer;

    public CheckIP() {
        File blackListFile = new File(FILEPATH);
        if (!blackListFile.exists()) {
            generateBlackListFile();
        }
        setBlackList();
        blackListBuffer = Collections.synchronizedList(new ArrayList<>());
    }

    private void setBlackList() {
        blackList = Collections.synchronizedList(new ArrayList<>());
        try {
            Path path = Paths.get(FILEPATH);
            blackList = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateBlackListFile() {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(FILEPATH));
            printWriter.println("This file has Black IP");
            printWriter.close();
        } catch (IOException ignored) {
        }
    }

    public boolean isBlackIP(InetSocketAddress address) {
        for (String savedIP : blackList) {
            if (Objects.isNull(savedIP)) {
                return false;
            }
            if (savedIP.equals(address.getHostName())) {
                return true;
            }
        }
        return false;
    }

    public void addBlackList(InetSocketAddress address) {
        blackListBuffer.add(address);
    }

    public void writeBlackList() {
        if (blackListBuffer.isEmpty()) {
            return;
        }
        Iterator<InetSocketAddress> iterator = blackListBuffer.iterator();
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(FILEPATH));
            while (iterator.hasNext()) {
                printWriter.println(iterator.next().getHostName());
            }
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
