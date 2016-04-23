package minn.minnbot.entities.command;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.custom.HelpSplitter;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.CommandManager;
import net.dv8tion.jda.entities.User;

import java.util.List;
import java.util.Random;

public class HelpCommand extends CommandAdapter {

    private User owner;
    private CommandManager manager;

    public HelpCommand(String prefix, Logger logger, CommandManager commands, User owner) {
        this.prefix = prefix;
        this.logger = logger;
        this.manager = commands;
        this.owner = owner;
    }

    @Override
    public void onCommand(CommandEvent event) {
        List<Command> commands = manager.getAllCommands();
        if (event.allArguments.isEmpty()) {
            String rngAlias = commands.get((new Random().nextInt(commands.size()))).getAlias();
            String s = "**__Example: " + prefix + "public__**\n";
            for (Command c : commands) {
                if(c.requiresOwner() && event.event.getAuthor() != owner)
                    continue;
                if (c instanceof HelpSplitter) {
                    if ((s + c.getAlias()).length() > 1000) {
                        event.sendMessage(s);
                        s = "\n";
                    }
                    if (c.getAlias().length() > 800) {
                        event.sendMessage("`" + c.getAlias() + "`");
                    } else {
                        s += "`" + c.getAlias() + "`\n";
                    }
                }
            }
            if (s.length() > 1)
                event.sendMessage(s);
            return;
        }
        String cmd = event.allArguments.split(" ", 2)[0];
        if (!event.allArguments.startsWith(prefix))
            cmd = prefix + cmd;
        for (Command c : commands) {
            if (c.isCommand(cmd)) {
                if (!c.usage().isEmpty())
                    event.sendMessage("Usage page for " + ((c instanceof HelpSplitter) ? "`" + c.getAlias() + "`" : cmd) + ": " + c.usage());
                else
                    event.sendMessage("No usage page given.");
                return;
            }
        }
        event.sendMessage("Unrecognised command/category `" + event.allArguments + "`\nUsage: " + usage());
    }

    @Override
    public boolean isCommand(String message) {
        try {
            message = message.toLowerCase();
            if (!message.startsWith(prefix))
                return false;
            message = message.substring(prefix.length());
            String command = message.split(" ", 2)[0];
            if (command.equalsIgnoreCase("help"))
                return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public String usage() {
        return "```xml\nhelp\nhelp <command>\nhelp <category>```\n**__Examples:__** `help public`, `help help`";
    }

    @Override
    public String getAlias() {
        return "help <command>";
    }

    @Override
    public String example() {
        return "help gif";
    }

}
