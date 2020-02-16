/*
 * CommandProcess.java
 * Author : Arakene
 * Created Date : 2020-02-14
 */
package com.thunder_cut.server;

import com.thunder_cut.server.data.Commands;
import com.thunder_cut.server.data.ReceivedData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class CommandProcess {


    private Map<Commands, BiConsumer<ReceivedData, String[]>> commandMap;
    private Processing.DisconnectCallBack disconnectCallBack;
    private Processing.ClientMapCallBack blindMapCallBack;

    public CommandProcess() {
        commandMap = new HashMap<>();
        commandMap.put(Commands.KICK, this::kick);
        commandMap.put(Commands.OP, this::op);
        commandMap.put(Commands.BLIND, this::blind);
        commandMap.put(Commands.IGNORE, this::ignore);
        commandMap.put(Commands.SET_NAME, this::setName);
    }

    private void kick(ReceivedData data, String[] tokens) {
        disconnectCallBack.disconnect(data.getSrc());
    }

    private void op(ReceivedData data, String[] tokens) {
        //클라한테 boolean값으로 소통 isClient = false면 클라기능만  true 로 바꾸면 서버기능까지
    }

    private void setName(ReceivedData data, String[] tokens) {
        data.getSrc().setName(tokens[1]);
    }

    //블라인드 해제도 만들어야됨
    private void blind(ReceivedData data, String[] tokens) {
        synchronized (blindMapCallBack.getClientGroup()){
            Map<ClientInfo, List<ClientInfo>> clientMap = blindMapCallBack.getClientGroup();
            for(ClientInfo client : clientMap.keySet()){
                clientMap.get(client).remove(data.getSrc());
            }
        }
    }

    //당연히 ignore 반대도 만들어야됨
    private void ignore(ReceivedData data, String[] tokens) {
        synchronized (blindMapCallBack.getClientGroup()){
            Map<ClientInfo, List<ClientInfo>> clientMap = blindMapCallBack.getClientGroup();
            for(ClientInfo client : clientMap.keySet()){
                if (client.getName().equals(tokens[1])){
                    clientMap.get(data.getSrc()).remove(client);
                    return;
                }
            }
        }
    }



    public Map<Commands, BiConsumer<ReceivedData, String[]>> getCommandMap() {
        return commandMap;
    }

    public void setDisconnectCallBack(Processing.DisconnectCallBack disconnectCallBack) {
        this.disconnectCallBack = disconnectCallBack;
    }

    public void setBlindMapCallBack(Processing.ClientMapCallBack blindMapCallBack) {
        this.blindMapCallBack = blindMapCallBack;
    }
}
