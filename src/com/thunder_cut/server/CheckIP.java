/*
 * CheckIP.java
 * Author : Arakene
 * Created Date : 2020-02-22
 */
package com.thunder_cut.server;

import java.io.*;

public class CheckIP {

    private static final String FILEPATH = "BlackList.txt";
    private BufferedReader bufferedReader;

    public CheckIP() {
        File blackListFile = new File(FILEPATH);
        try {
            if(!blackListFile.exists()){
                generateBlackList();
            }
            bufferedReader = new BufferedReader(new FileReader(FILEPATH));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isBlackIP(String IP) {
        while(true){
            try{
                String savedIP = bufferedReader.readLine();
                if(savedIP == null){
                    return false;
                }
                if(savedIP.equals(IP)){
                    addBlackList(IP);
                    return true;
                }
                else{
                    return false;
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void generateBlackList(){
        try{
            addBlackList("This file has Black IP");
        }
        catch (IOException ignored){
        }
    }

    private void addBlackList(String IP) throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter(FILEPATH, true));
        printWriter.println(IP);
        printWriter.close();
    }
}
