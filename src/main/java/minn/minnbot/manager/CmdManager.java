package minn.minnbot.manager;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public abstract class CmdManager {

    protected AtomicReference<String> err;
    protected List<String> errors;
    protected Logger logger;
    private List<Command> commands = new LinkedList<>();
    private Map<Command, Boolean> disable = new HashMap<>();
    protected boolean ownerOnly = false;

    public List<Command> getCommands() {
        return Collections.unmodifiableList(((commands == null) ? new LinkedList<>() : commands));
    }

    public List<String> getErrors() {
        if (errors == null) {
            return Collections.unmodifiableList(new LinkedList<>());
        }
        return Collections.unmodifiableList(errors);
    }

    public void shuffle() {
        Collections.shuffle(commands);
    }

    protected String registerCommand(Command command) {
        if (command == null) {
            return "NullPointerException";
        }
        commands.add(command);
        disable.put(command, false);
        return "";
    }

    public void call(MessageReceivedEvent event) {
        commands.parallelStream().forEach(c -> {
            if (!disable.get(c)) c.onMessageReceived(event);
        });
    }

    public void callAsync(MessageReceivedEvent event, Consumer<Boolean> consumer) {
        commands.parallelStream().forEach(c -> {
            if (!disable.get(c)) c.onMessageReceived(event);
        });
        if (consumer != null) {
            consumer.accept(true);
        }
    }

    public boolean requiresOwner() {
        return ownerOnly;
    }

    public void disable(Command cmd, boolean input) {
        if (disable.containsKey(cmd)) {
            disable.replace(cmd, disable.get(cmd), input);
        }
    }

}
