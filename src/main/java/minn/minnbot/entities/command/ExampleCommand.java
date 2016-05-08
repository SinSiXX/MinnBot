package minn.minnbot.entities.command;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.CommandManager;

import java.util.List;

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
    public boolean isCommand(String message, List<String> prefixList) {
        String[] p = message.split(" ", 2);
        if (p.length < 1)
            return false;
        if (p[0].equalsIgnoreCase(prefix + "example"))
            return true;
        for (String fix : prefixList) {
            if (p[0].equalsIgnoreCase(fix + "example"))
                return true;
        }
        return false;
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
