/*
 * CommandExecutor.java
 * Author : Arakene
 * Created Date : 2020-02-26
 */
package com.thunder_cut.feature;

import com.thunder_cut.connection.Requests;
import com.thunder_cut.data.ConnectedClient;
import com.thunder_cut.data.DataType;
import com.thunder_cut.data.ReceivedData;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public class CommandExecutor {

    private ReceivedData receivedData;
    private Requests requests;
    private Map<String, BiConsumer<Integer, String>> commandExecutor;

    public CommandExecutor(ReceivedData receivedData, Requests requests) {
        this.receivedData = receivedData;
        this.requests = requests;
        initializeCommandExecutor();
    }

    private void initializeCommandExecutor() {
        commandExecutor = new HashMap<>();
        commandExecutor.put("/set_name", this::setName);
        commandExecutor.put("/op", this::op);
        commandExecutor.put("/ignore", this::ignore);
        commandExecutor.put("/blind", this::blind);
        commandExecutor.put("/kick", this::kick);
    }


    /**
     * Identify DataType and execute command by given type
     */
    public void execute() {
        String commandline = new String(receivedData.getBuffer().array());
        String[] args = commandline.split(" ");
        String commandIdentifier = args[0].trim();
        try {
            commandExecutor.get(commandIdentifier).accept(receivedData.getSrcID(), args[1].trim());
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    private void setName(int srcID, String arg) {
        requestWriteCommandMessage(srcID, " Changed name -> " + arg);
        requests.getClient(srcID).setName(arg);
    }

    private void op(int srcID, String arg) {
        if (requests.getClient(srcID).isOp()) {
            requestWriteCommandMessage(srcID, "Set OP to " + arg);
            requests.getClient(requests.getID(arg)).setOP(true);
        }
    }

    private void ignore(int srcID, String arg) {
        if(requests.getClient(requests.getID(arg)).getIgnoreList().contains(srcID)){
            requests.getClient(requests.getID(arg)).getIgnoreList().remove(srcID);
            return;
        }
        requests.getClient(requests.getID(arg)).getIgnoreList().add(srcID);
    }

    private void blind(int srcID, String arg) {
        ConnectedClient client = requests.getClient(srcID);
        if (client.getIgnoreList().contains(srcID)) {
            client.getIgnoreList().clear();
            return;
        }
        for (int i = 0; ; i++) {
            if (Objects.isNull(requests.getClient(i))) {
                return;
            }
            client.getIgnoreList().add(i);
        }
    }

    private void kick(int srcID, String arg) {
        if (!requests.getClient(srcID).isOp()) {
            return;
        }
        requestWriteCommandMessage(srcID, "Kick " + arg);
        requests.disconnect(requests.getClient(requests.getID(arg)));
    }

    /*
    /ban 의 처리
    또 request? 이게 제일 빠르고 쉬움


     */

    private void requestWriteCommandMessage(int srcID, String message) {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        ReceivedData commandMessage = new ReceivedData(DataType.MSG, buffer, srcID, requests.getClient(srcID).getName());
        requests.requestWriteToClient(commandMessage);
    }
}
