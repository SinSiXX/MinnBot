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
    private boolean operator;

    public HelpSplitter (String content, String name, String prefix, boolean operator) {
        if(content == null || content.isEmpty() || name == null || content.isEmpty())
            throw new UnsupportedOperationException("Splitter contents may never be empty or null.");
        this.content = "\n[" + content.replace("`", "\\`").toUpperCase() + "] (" + name + ")\n";
        this.name = name;
        this.prefix = prefix;
        this.operator = operator;
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
    public boolean isCommand(String message, List<String> prefixList) {
        String[] p = message.split(" ", 2);
        if(p.length < 1)
            return false;
        if(p[0].equalsIgnoreCase(prefix + name))
            return true;
        for(String fix : prefixList) {
            if(p[0].equalsIgnoreCase(fix + name))
                return true;
        }
        return false;
    }


    @Override
    public boolean requiresOwner() {
        return operator;
    }

    @Override
    public String example() {
        return "help " + name;
    }
}
