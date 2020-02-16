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

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Processing {

    interface ClientMapCallBack {
        Map<ClientInfo, List<ClientInfo>> getClientGroup();
    }

    interface DisconnectCallBack {
        void disconnect(ClientInfo client);
    }

    private Map<DataType, Consumer<ReceivedData>> processMap;
    private ClientMapCallBack mapCallBack;
    private DisconnectCallBack disconnectCallBack;
    private CommandProcess commandProcess;
    private BiConsumer<ClientInfo, SendingData> write;

    public Processing() {
        processMap = new HashMap<>();
        processMap.put(DataType.CMD, this::command);
        processMap.put(DataType.MSG, this::message);
        processMap.put(DataType.IMG, this::image);
        commandProcess = new CommandProcess();
        commandProcess.setDisconnectCallBack(disconnectCallBack);
        commandProcess.setBlindMapCallBack(mapCallBack);
    }

    public void setCallBack(ClientMapCallBack groupCallBack, DisconnectCallBack disconnectCallBack) {
        this.mapCallBack = groupCallBack;
        this.disconnectCallBack = disconnectCallBack;
    }

    public void setWrite(BiConsumer<ClientInfo, SendingData> write){
        this.write = write;
    }
    private void command(ReceivedData data) {
        ByteBuffer buffer = data.getBuffer();
        buffer.flip();
        String command = new String(buffer.array());
        String[] commandToken = new String[3];
        StringTokenizer stringTokenizer = new StringTokenizer(command, " ");
        int index = 0;
        while (stringTokenizer.hasMoreTokens()) {
            commandToken[index++] = stringTokenizer.nextToken();
        }
        commandProcess.getCommandMap().get(Commands.getCommand(commandToken[0])).accept(data, commandToken);
    }

    private void message(ReceivedData data) {
        synchronized (mapCallBack.getClientGroup()) {
            for (ClientInfo dest : mapCallBack.getClientGroup().get(data.getSrc())) {
                SendingData sendingData = new SendingData(data.getSrc(), dest, data.getDataType(), data.getBuffer().array());
                write.accept(dest, sendingData);
            }
        }
    }

    //TODO 필드 넘기지말고 함수로 만들어서 넘길 것
    private void image(ReceivedData data) {
        synchronized (mapCallBack.getClientGroup()) {
            for (ClientInfo dest : mapCallBack.getClientGroup().get(data.getSrc())) {
                SendingData sendingData = new SendingData(data.getSrc(), dest, data.getDataType(), data.getBuffer().array());
                write.accept(dest, sendingData);
            }
        }
    }

    public Map<DataType, Consumer<ReceivedData>> getProcessMap() {
        return processMap;
    }
}
