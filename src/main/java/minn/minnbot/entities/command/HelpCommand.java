package minn.minnbot.entities.command;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.custom.HelpSplitter;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.CommandManager;

import java.util.List;

public class HelpCommand extends CommandAdapter {

    private String owner;
    private CommandManager manager;

    public HelpCommand(String prefix, Logger logger, CommandManager commands, String owner) {
        init(prefix, logger);
        this.manager = commands;
        this.owner = owner;
    }

    @Override
    public void onCommand(CommandEvent event) {
        List<Command> commands = manager.getAllCommands();
        if (event.allArguments.isEmpty()) {
            final String[] s = {"**__ALL COMMANDS REQUIRE A PREFIX__**\n**__Example: " + prefix + "help public__**\n"};
            commands.parallelStream().filter(c -> !c.requiresOwner() || event.event.getAuthor().getId().equals(owner)).forEachOrdered((c) -> {
                if (c instanceof HelpSplitter) {
                    if ((s[0] + c.getAlias()).length() > 1000) {
                        event.sendMessage(s[0]);
                        s[0] = "\n";
                    }
                    if (c.getAlias().length() > 800) {
                        event.sendMessage("`" + c.getAlias() + "`");
                    } else {
                        s[0] += "`" + c.getAlias() + "`\n";
                    }
                }
            });
            if (s[0].length() > 1)
                event.sendMessage(s[0]);
            return;
        }
        String input = event.allArguments.split("\\s+", 2)[0];
        String cmd = input;
        if (!event.allArguments.startsWith(prefix))
            input = prefix + cmd;
        for (Command c : commands) {
            if (c.isCommand(input, CommandManager.getPrefixList(event.guild.getId()))) {
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
