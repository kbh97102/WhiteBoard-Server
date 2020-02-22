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
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
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
            errorMessage(data.getSrc(), clientMap);
        }
    }

    private void errorMessage(ClientInformation src, Map<ClientInformation, List<ClientInformation>> clientMap) {
        String errorMessage = "Error! Given command do not exist";
        int srcIndex = 0;
        for (int destIndex = 0; destIndex < clientMap.get(src).size(); destIndex++) {
            ClientInformation destination = clientMap.get(src).get(destIndex);
            if (src == destination) {
                srcIndex = destIndex;
                SendingData sendingData = new SendingData(srcIndex, destIndex, DataType.MSG, errorMessage.getBytes());
                write(src, destination, sendingData);
                return;
            }
        }
    }

    /**
     * Generate message or image data and write to all client in client list
     *
     * @param data      this have data, type, src
     * @param clientMap client list
     */
    private void notCommand(ReceivedData data, Map<ClientInformation, List<ClientInformation>> clientMap) {
        int srcIndex = 0;
        for (int index = 0; index < clientMap.get(data.getSrc()).size(); index++) {
            ClientInformation destination = clientMap.get(data.getSrc()).get(index);
            if (data.getSrc() == destination) {
                srcIndex = index;
            }
            SendingData sendingData = new SendingData(srcIndex, index, data.getDataType(), data.getBuffer().array());
            write(data.getSrc(), destination, sendingData);
        }
    }

    /**
     * Write to client with given data
     *
     * @param dest client who received data
     * @param data data for write
     */
    private synchronized void write(ClientInformation src,ClientInformation dest, SendingData data) {
        try {
            dest.getClient().write(data.generateDataByType(src));
        } catch (IOException | NullPointerException e) {
            disconnect.accept(dest);
        }
    }

    public void processWithType(ReceivedData data, Map<ClientInformation, List<ClientInformation>> clientMap) {
        if (data.getDataType().equals(DataType.CMD)) {
            command(data, clientMap);
        } else {
            notCommand(data, clientMap);
        }
    }
}
