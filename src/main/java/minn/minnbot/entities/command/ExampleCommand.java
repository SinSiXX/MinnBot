package minn.minnbot.entities.command;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.CommandManager;

public class ExampleCommand extends CommandAdapter {

    private CommandManager manager;

    public ExampleCommand(String prefix, Logger logger, CommandManager manager) {
        this.logger = logger;
        this.prefix = prefix;
        this.manager = manager;
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.allArguments.isEmpty()) {
            event.sendMessage("Usage: `" + prefix + "example <command>`");
            return;
        }
        Command com = manager.getAllCommands().parallelStream().filter(
                (cmd) -> cmd.isCommand((
                                (event.allArguments.startsWith(prefix))
                                        ? event.allArguments
                                        : prefix + event.allArguments),
                        CommandManager.getPrefixList(event.guild.getId()))).findFirst().orElse(null);
        if (com == null) {
            event.sendMessage("Not a known command!");
            return;
        }
        event.sendMessage(prefix + com.example());
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
