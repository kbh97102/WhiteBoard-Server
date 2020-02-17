/*
 * CommandProcess.java
 * Author : Arakene
 * Created Date : 2020-02-14
 */
package com.thunder_cut.server;

import com.thunder_cut.server.data.Commands;
import com.thunder_cut.server.data.ReceivedData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This work if data type is command with given data
 */
public class CommandProcess {


    private Map<Commands, BiConsumer<ReceivedData, String[]>> commandMap;
    private Map<ClientInfo, List<ClientInfo>> clientMap;
    private Consumer<ClientInfo> disconnect;

    public CommandProcess(Map<ClientInfo, List<ClientInfo>> clientMap, Consumer<ClientInfo> disconnect) {
        commandMap = new HashMap<>();
        commandMap.put(Commands.KICK, this::kick);
        commandMap.put(Commands.OP, this::op);
        commandMap.put(Commands.BLIND, this::blind);
        commandMap.put(Commands.IGNORE, this::ignore);
        commandMap.put(Commands.SET_NAME, this::setName);
        this.clientMap = clientMap;
        this.disconnect = disconnect;
    }

    /**
     * Disconnect specific client
     * @param data data, type, src
     * @param tokens split command
     */
    private void kick(ReceivedData data, String[] tokens) {
        disconnect.accept(getDest(tokens[1]));
    }

    private void op(ReceivedData data, String[] tokens) {
        //클라한테 boolean값으로 소통 isClient = false면 클라기능만  true 로 바꾸면 서버기능까지
    }

    /**
     * Set client name by given data
     * @param data data, type, src
     * @param tokens split command
     */
    private void setName(ReceivedData data, String[] tokens) {
        data.getSrc().setName(tokens[1]);
    }

    /**
     * Stop write my data to all client
     * 모든 key를 돌면서 자기를 삭제
     * 리스트에 없으면 추가 있으면 삭제
     *
     * @param data data, type, src
     * @param tokens split command
     */
    private void blind(ReceivedData data, String[] tokens) {
        for (ClientInfo key : clientMap.keySet()) {
            if (!clientMap.get(key).contains(data.getSrc())) {
                clientMap.get(key).add(data.getSrc());
            } else {
                clientMap.get(key).remove(data.getSrc());
            }
        }
    }

    /**
     * Block send data from client that given data
     * 자기꺼 리스트에 해당상대있으면 삭제
     * 리스트에 없으면 추가 있으면 삭제
     *
     * @param data   data, type, src
     * @param tokens split command
     */
    private void ignore(ReceivedData data, String[] tokens) {
        ClientInfo dest = getDest(tokens[1]);
        if (!clientMap.get(data.getSrc()).contains(dest)) {
            clientMap.get(data.getSrc()).add(dest);
        } else {
            clientMap.get(data.getSrc()).remove(dest);
        }
    }

    /**
     * Find Client include given name
     * If don't exist return null
     * @param clientName client's name
     * @return clientinfo that include clientName
     */
    private ClientInfo getDest(String clientName) {
        for (List<ClientInfo> list : clientMap.values()) {
            for (ClientInfo client : list) {
                if (client.getName().equals(clientName)) {
                    return client;
                }
            }
        }
        return null;
    }

    public Map<Commands, BiConsumer<ReceivedData, String[]>> getCommandMap() {
        return commandMap;
    }

}
