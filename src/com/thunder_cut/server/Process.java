/*
 * Processing.java
 * Author : Arakene
 * Created Date : 2020-02-14
 */
package com.thunder_cut.server;

import com.thunder_cut.server.data.Commands;
import com.thunder_cut.server.data.DataType;
import com.thunder_cut.server.data.ReceivedData;
import com.thunder_cut.server.data.SendingData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Process {

    private Map<DataType, BiConsumer<ReceivedData, Map<ClientInfo, List<ClientInfo>>>> processMap;
    private Consumer<ClientInfo> disconnect;

    public Process(Consumer<ClientInfo> disconnect) {
        processMap = new HashMap<>();
        processMap.put(DataType.CMD, this::command);
        processMap.put(DataType.MSG, this::message);
        processMap.put(DataType.IMG, this::image);
        this.disconnect = disconnect;
    }

    private void command(ReceivedData data, Map<ClientInfo, List<ClientInfo>> clientMap) {
        ByteBuffer buffer = data.getBuffer();
        buffer.flip();
        String command = new String(buffer.array());
        String[] commandToken = new String[3];
        StringTokenizer stringTokenizer = new StringTokenizer(command, " ");
        int index = 0;
        while (stringTokenizer.hasMoreTokens()) {
            commandToken[index++] = stringTokenizer.nextToken();
        }
        CommandProcess commandProcess = new CommandProcess(clientMap, disconnect);
        try{
            commandProcess.getCommandMap().get(Commands.acceptable(commandToken[0],data.getSrc().isOp())).accept(data, commandToken);
        }
        catch (NullPointerException e){
            String errorMessage = "돌아가 어림도없어임마";
            SendingData error = new SendingData(data.getSrc(), data.getSrc(), DataType.MSG,errorMessage.getBytes());
            write(data.getSrc() ,error);
        }
    }

    private void message(ReceivedData data, Map<ClientInfo, List<ClientInfo>> clientMap) {
        for (ClientInfo dest : clientMap.get(data.getSrc())) {
            SendingData sendingData = new SendingData(data.getSrc(), dest, data.getDataType(), data.getBuffer().array());
            write(dest, sendingData);
        }
    }

    private void image(ReceivedData data, Map<ClientInfo, List<ClientInfo>> clientMap) {
        for (ClientInfo dest : clientMap.get(data.getSrc())) {
            SendingData sendingData = new SendingData(data.getSrc(), dest, data.getDataType(), data.getBuffer().array());
            write(dest, sendingData);
        }
    }

    private synchronized void write(ClientInfo dest, SendingData data) {
        try {
            dest.getClient().write(data.identifyType());
        } catch (IOException e) {
            disconnect.accept(dest);
        }
    }

    public Map<DataType, BiConsumer<ReceivedData, Map<ClientInfo, List<ClientInfo>>>> getProcessMap() {
        return processMap;
    }
}
