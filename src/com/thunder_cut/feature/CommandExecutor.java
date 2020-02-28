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
    private Map<String, BiConsumer<Integer, String[]>> commandExecutor;

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
            commandExecutor.get(commandIdentifier).accept(receivedData.getSrcID(), args);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    private void setName(int srcID, String[] args) {
        requestWriteCommandMessage(srcID, " Changed name -> " + args[1]);
        requests.getClient(srcID).setName(args[1].trim());
    }

    private void op(int srcID, String[] args) {
        if (requests.getClient(srcID).isOp()) {
            requestWriteCommandMessage(srcID, "Set OP to " + args[1]);
            requests.getClient(requests.getID(args[1].trim())).setOP(true);
        }
    }

    private void ignore(int srcID, String[] args) {
        if(requests.getClient(requests.getID(args[1].trim())).getIgnoreList().contains(srcID)){
            requests.getClient(requests.getID(args[1].trim())).getIgnoreList().remove(srcID);
            return;
        }
        requests.getClient(requests.getID(args[1].trim())).getIgnoreList().add(srcID);
    }

    private void blind(int srcID, String[] args) {
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

    private void kick(int srcID, String[] args) {
        if (!requests.getClient(srcID).isOp()) {
            return;
        }
        requestWriteCommandMessage(srcID, "Kick " + args[1]);
        requests.disconnect(requests.getClient(requests.getID(args[1].trim())));
    }

    private void requestWriteCommandMessage(int srcID, String message) {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        ReceivedData commandMessage = new ReceivedData(DataType.MSG, buffer, srcID, requests.getClient(srcID).getName());
        requests.requestWriteToClient(commandMessage);
    }
}
