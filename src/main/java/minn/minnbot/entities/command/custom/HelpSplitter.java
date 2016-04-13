package minn.minnbot.entities.command.custom;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.HelpCommand;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.LinkedList;
import java.util.List;

public class HelpSplitter implements Command {

    private String content;
    public List<Command> commands = new LinkedList<>();
    private String name;
    private String prefix;

    public HelpSplitter (String content, String name, String prefix) {
        if(content == null || content.isEmpty() || name == null || content.isEmpty())
            throw new UnsupportedOperationException("Splitter contents may never be empty or null.");
        this.content = "\n[" + content.replace("`", "\\`").toUpperCase() + "] (" + name + ")\n";
        this.name = name;
        this.prefix = prefix;
    }

    public boolean add(Command com) {
        if(com == null || commands.contains(com))
            return false;
        commands.add(com);
        return true;
    }

    public String getAlias() {
        return content;
    }

    public String usage() {
        String output = "```xml\n";
        for(Command com : commands) {
            if (!(com instanceof HelpCommand)) {
                String alias = com.getAlias().replace("`", "");
                if((output + "\n" + ((com.requiresOwner()) ? "[OP] " : "") + alias).length() >= 1000) {
                    return output + "\n...```";
                }
                output += "\n" + ((com.requiresOwner()) ? "[OP] " : "") + alias;
            }
        }
        return output + "```";
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

    }

    @Override
    public void setLogger(Logger logger) {

    }

    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public void onCommand(CommandEvent event) {

    }

    @Override
    public boolean isCommand(String message) {
        String[] parts = message.split(" ", 2);
        if(parts.length < 1)
            return false;
        String command = parts[0];
        return command.equalsIgnoreCase(prefix + name);
    }


    @Override
    public boolean requiresOwner() {
        return false;
    }
}
