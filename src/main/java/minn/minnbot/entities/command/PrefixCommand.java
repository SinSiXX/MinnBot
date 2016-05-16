package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.CommandManager;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class PrefixCommand extends CommandAdapter {

    private String owner;

    public PrefixCommand(String prefix, Logger logger, String owner) {
        init(prefix, logger);
        this.owner = owner;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isPrivate())
            return;
        super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.author != event.guild.getOwner() && event.author != event.jda.getUserById(owner)) {
            event.sendMessage("You are not authorized to add or remove a server prefix.");
            return;
        }
        if (event.allArguments.isEmpty()) {
            event.sendMessage(String.format("Usage: %s\n**__Prefixes:__ %s**", usage(), CommandManager.getPrefixList(event.guild.getId())));
            return;
        }
        if (event.arguments.length < 2) {
            event.sendMessage("Missing parameter(s). Use the help command for further instructions.");
            return;
        }
        String method = event.arguments[0];
        String prefix = event.arguments[1];
        if (prefix.equalsIgnoreCase(this.prefix))
            event.sendMessage("You are not able to modify the default prefix.");
        else if (method.equalsIgnoreCase("add")) {
            if (!CommandManager.addPrefix(event.guild, prefix))
                event.sendMessage("Prefix already registered.");
            else
                event.sendMessage("Prefix has been added to the list.");
        } else if (method.equalsIgnoreCase("remove")) {
            if (!CommandManager.removePrefix(event.guild, prefix))
                event.sendMessage("Prefix is not recognized. Or you are not authorized to access it.");
            else
                event.sendMessage("Prefix has been removed from the list.");
        }
    }

    public String usage() {
        return "Adds/Removes a server specific prefix! **OWNER ONLY**";
    }

    @Override
    public String getAlias() {
        return "prefix <add/remove> <fix>";
    }

    public String example() {
        return "prefix add !";
    }
}
