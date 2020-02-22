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

    private Map<DataType, BiConsumer<ReceivedData, Map<ClientInfo, List<ClientInfo>>>> processMap;
    private Consumer<ClientInfo> disconnect;

    public Process(Consumer<ClientInfo> disconnect) {
        processMap = new EnumMap<DataType, BiConsumer<ReceivedData, Map<ClientInfo, List<ClientInfo>>>>(DataType.class);
        processMap.put(DataType.CMD, this::command);
        processMap.put(DataType.MSG, this::message);
        processMap.put(DataType.IMG, this::image);
        this.disconnect = disconnect;
    }

    /**
     * This work if type is command
     * Split command and work with given command in command process
     *
     * @param data      this have data, type, src
     * @param clientMap client list
     */
    private void command(ReceivedData data, Map<ClientInfo, List<ClientInfo>> clientMap) {
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
    private void message(ReceivedData data, Map<ClientInfo, List<ClientInfo>> clientMap) {
        for (ClientInfo dest : clientMap.get(data.getSrc())) {
            SendingData sendingData = new SendingData(data.getSrc(), dest, data.getDataType(), data.getBuffer().array());
            write(dest, sendingData);
        }
    }

    /**
     * Generate image data and write to all client in client list
     *
     * @param data      this have data, type, src
     * @param clientMap client list
     */
    private void image(ReceivedData data, Map<ClientInfo, List<ClientInfo>> clientMap) {
        for (ClientInfo dest : clientMap.get(data.getSrc())) {
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
    private synchronized void write(ClientInfo dest, SendingData data) {
        try {
            dest.getClient().write(data.identifyType());
        } catch (IOException | NullPointerException e) {
            disconnect.accept(dest);
        }
    }

    public Map<DataType, BiConsumer<ReceivedData, Map<ClientInfo, List<ClientInfo>>>> getProcessMap() {
        return processMap;
    }
}
