package minn.minnbot.entities.command.roles;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.utils.PermissionUtil;

public class CreateRoleCommand extends ListenerAdapter implements Command{

    private String prefix;
    private Logger logger;

    public CreateRoleCommand(Logger logger, String prefix) {
        this.prefix = prefix;
        this.logger = logger;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.isPrivate())
            return;
        else if(isCommand(event.getMessage().getContent())) {
            if(!PermissionUtil.checkPermission(event.getAuthor(), Permission.MANAGE_ROLES, event.getGuild()))
                event.getChannel().sendMessageAsync("You are not allowed to manage roles. :thumbsdown::skin-tone-" + ((int)Math.ceil(Math.random() * 5)) + ":", null);
            else {
                logger.logCommandUse(event.getMessage());
                onCommand(new CommandEvent(event));
            }
        }
    }

    @Override
    public void setLogger(Logger logger) {
        if (logger == null)
            throw new IllegalArgumentException("Logger cannot be null");
        this.logger = logger;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void onCommand(CommandEvent event) {
       if(!PermissionUtil.checkPermission(event.event.getJDA().getSelfInfo(), Permission.MANAGE_ROLES, event.event.getGuild())) {
           event.sendMessage("I am unable to manage roles. :thumbsdown::skin-tone-" + ((int)Math.ceil(Math.random() * 5)) + ":");
           return;
       }
        event.event.getGuild().createRole().setName(((event.allArguments.isEmpty()) ? "new role" : event.allArguments)).update();
        event.sendMessage(":thumbsup::skin-tone-" + ((int)Math.ceil(Math.random() * 5)) + ":");
    }

    @Override
    public boolean isCommand(String message) {
        try {
            message = message.toLowerCase();
            if (!message.startsWith(prefix))
                return false;
            message = message.substring(prefix.length());
            String command = message.split(" ", 2)[0];
            if (command.equalsIgnoreCase("createrole"))
                return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public String usage() {
        return "`createrole <name>` or just `createrole`";
    }

    @Override
    public String getAlias() {
        return "createrole <name>";
    }

    @Override
    public boolean requiresOwner() {
        return false;
    }
}
