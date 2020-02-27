/*
 * CommandExecutor.java
 * Author : Arakene
 * Created Date : 2020-02-26
 */
package com.thunder_cut.feature;

import com.thunder_cut.connection.Requests;
import com.thunder_cut.data.ReceivedData;

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

    public void execute() {
        String commandline = new String(receivedData.getBuffer().array());
        String[] args = commandline.split(" ");
        String commandIdentifier = args[0].trim();
        if (!Objects.isNull(args[1])) {
            commandExecutor.get(commandIdentifier).accept(receivedData.getSrcID(), args);
        }
    }

    private void setName(int srcID, String[] args) {
        requests.getClient(srcID).setName(args[1].trim());
    }

    private void op(int srcID, String[] args) {
        if (requests.getClient(srcID).isOp()) {
            requests.getClient(requests.getID(args[1].trim())).setOP(true);
        }
    }

    private void ignore(int srcID, String[] args) {
        for (int i = 0; ; i++) {
            if (requests.getClient(i) == null) {
                return;
            }
            if (requests.getClient(i).getIgnoreList().contains(srcID)) {
                if (requests.getClient(i).getIgnoreList().size() == 1) {
                    requests.getClient(i).getIgnoreList().clear();
                } else {
                    requests.getClient(i).getIgnoreList().remove(srcID);
                }
            } else {
                requests.getClient(i).getIgnoreList().add(srcID);
            }
        }
    }

    private void blind(int srcID, String[] args) {
        if (requests.getClient(srcID).getIgnoreList().contains(requests.getID(args[1].trim()))) {
            if (requests.getClient(srcID).getIgnoreList().size() == 1) {
                requests.getClient(srcID).getIgnoreList().clear();
            } else {
                requests.getClient(srcID).getIgnoreList().remove(requests.getID(args[1].trim()));
            }
        } else {
            requests.getClient(srcID).getIgnoreList().add(requests.getID(args[1].trim()));
        }
    }

    private void kick(int srcID, String[] args) {
        if (!requests.getClient(srcID).isOp()) {
            return;
        }
        requests.disconnect(requests.getClient(requests.getID(args[1].trim())));
    }
}
