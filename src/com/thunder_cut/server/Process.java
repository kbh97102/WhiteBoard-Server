/*
 * Processing.java
 * Author : Arakene
 * Created Date : 2020-02-14
 */
package com.thunder_cut.server;

import com.thunder_cut.server.data.CommandType;
import com.thunder_cut.server.data.DataType;
import com.thunder_cut.server.data.ReceivedData;
import com.thunder_cut.server.data.SendingData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * After Receiving data, work by data type
 */
public class Process {

    private Consumer<ClientInformation> disconnect;

    public Process(Consumer<ClientInformation> disconnect) {
        this.disconnect = disconnect;
    }

    /**
     * This work if type is command
     * Split command and work with given command in command process
     *
     * @param data      this have data, type, src
     * @param clientMap client list
     */
    private void command(ReceivedData data, Map<ClientInformation, List<ClientInformation>> clientMap) {
        ByteBuffer buffer = data.getBuffer();
        buffer.flip();
        String command = new String(buffer.array(), StandardCharsets.UTF_8);
        String[] commandToken = new String[3];
        StringTokenizer stringTokenizer = new StringTokenizer(command, " ");
        int index = 0;
        while (stringTokenizer.hasMoreTokens()) {
            commandToken[index++] = stringTokenizer.nextToken();
        }
        CommandProcess commandProcess = new CommandProcess(clientMap, disconnect);
        try {
            commandProcess.getCommandMap().get(CommandType.acceptable(commandToken[0], data.getSrc().isOp())).accept(data, commandToken);
        } catch (NullPointerException e) {
            String errorMessage = "Error! Given command do not exist";
            SendingData error = new SendingData(data.getSrc(), data.getSrc(), DataType.MSG, errorMessage.getBytes());
            write(data.getSrc(), error);
        }
    }

    /**
     * Generate message data and write to all client in client list
     *
     * @param data      this have data, type, src
     * @param clientMap client list
     */
    private void message(ReceivedData data, Map<ClientInformation, List<ClientInformation>> clientMap) {
        for (ClientInformation dest : clientMap.get(data.getSrc())) {
            SendingData sendingData = new SendingData(data.getSrc(), dest, data.getDataType(), data.getBuffer().array());
            write(dest, sendingData);
        }
        for(int i=0;i<clientMap.get(data.getSrc()).size();i++){
            ClientInformation destination = clientMap.get(data.getSrc()).get(i);
            SendingData sendingData = new SendingData(data.getSrc(), destination, data.getDataType(), data.getBuffer().array());
            write(destination, sendingData);
        }
    }

    /**
     * Generate image data and write to all client in client list
     *
     * @param data      this have data, type, src
     * @param clientMap client list
     */
    private void image(ReceivedData data, Map<ClientInformation, List<ClientInformation>> clientMap) {
        for (ClientInformation dest : clientMap.get(data.getSrc())) {
            SendingData sendingData = new SendingData(data.getSrc(), dest, data.getDataType(), data.getBuffer().array());
            write(dest, sendingData);
        }
    }

    /**
     * Write to client with given data
     *
     * @param dest client who received data
     * @param data data for write
     */
    private synchronized void write(ClientInformation dest, SendingData data) {
        try {
            dest.getClient().write(data.generateDataByType());
        } catch (IOException | NullPointerException e) {
            disconnect.accept(dest);
        }
    }

    public void processWithType(ReceivedData data, Map<ClientInformation, List<ClientInformation>> clientMap){
        if(data.getDataType().equals(DataType.MSG)){
            message(data, clientMap);
        }
        else if(data.getDataType().equals(DataType.IMG)){
            image(data, clientMap);
        }
        else{
            command(data, clientMap);
        }
    }
}
