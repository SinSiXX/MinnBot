package minn.minnbot.entities.command;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

import java.util.List;

public class ExampleCommand extends CommandAdapter {

    private List<Command> commands;

    public ExampleCommand(String prefix, Logger logger, List<Command> commands) {
        this.logger = logger;
        this.prefix = prefix;
        this.commands = commands;
    }

    @Override
    public void onCommand(CommandEvent event) {
        if(event.allArguments.isEmpty()) {
            event.sendMessage("Usage: `" + prefix + "example <command>`");
            return;
        }
        Command com = commands.parallelStream().filter((cmd) -> cmd.isCommand(((event.allArguments.startsWith(prefix)) ? event.allArguments : prefix + event.allArguments))).findFirst().orElse(null);
        if(com == null) {
            event.sendMessage("Not a known command!");
            return;
        }
        event.sendMessage(prefix + com.example());
    }

    @Override
    public boolean isCommand(String message) {
        String[] parts = message.split(" ",2);
        return parts.length > 0 && parts[0].equalsIgnoreCase(prefix + "example");
    }

    @Override
    public String getAlias() {
        return "example <command>";
    }

    @Override
    public String example() {
        return "INCEPTION!!!1!1!!";
    }
}
