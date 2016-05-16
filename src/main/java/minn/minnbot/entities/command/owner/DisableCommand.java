package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.entities.throwable.Info;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.CmdManager;
import minn.minnbot.manager.CommandManager;
import minn.minnbot.util.EmoteUtil;

import java.util.LinkedList;
import java.util.List;

public class DisableCommand extends CommandAdapter {

    private CommandManager manager;

    public DisableCommand(String prefix, Logger logger, CommandManager manager) {
        init(prefix, logger);
        this.manager = manager;
    }

    @Override
    public void onCommand(CommandEvent event) {
        List<CmdManager> managerList = manager.getManagers();
        List<Command> commandList = manager.getAllCommands();
        final Command[] cmd = {null};
        String commandName = event.allArguments.trim();
        if(!commandName.startsWith(prefix))
            commandName = prefix + commandName;
        String finalCommandName = commandName;
        commandList.forEach(c -> {
            if(cmd[0] == null && c.isCommand(finalCommandName, new LinkedList<>())) {
                cmd[0] = c;
            }
        });
        if(cmd[0] == null) {
            event.sendMessage("Command is not known.");
            return;
        }
        if(cmd[0] instanceof EnableCommand) {
            event.sendMessage("You're not allowed to disable the enable command m8, pls");
            return;
        }
        managerList.forEach((manager1 -> manager1.disable(cmd[0], true)));
        event.sendMessage("Disabled `" + cmd[0].getAlias() + "`. " + EmoteUtil.getRngOkHand());
        cmd[0].getLogger().logThrowable(new Info("[" + cmd[0].getClass().getSimpleName() + "] Disabled"));
    }

    @Override
    public String getAlias() {
        return "disable <command>";
    }

    public String example() {
        return "disable gif";
    }

    public boolean requiresOwner() {
        return true;
    }
}
