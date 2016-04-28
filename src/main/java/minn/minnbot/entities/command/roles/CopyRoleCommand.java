package minn.minnbot.entities.command.roles;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.RoleUtil;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.utils.PermissionUtil;

public class CopyRoleCommand extends CommandAdapter {

    public CopyRoleCommand(Logger logger, String prefix) {
        this.prefix = prefix;
        this.logger = logger;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.isPrivate())
            return;
        if(isCommand(event.getMessage().getContent())) {
            if(!PermissionUtil.checkPermission(event.getAuthor(), Permission.MANAGE_ROLES, event.getGuild()))
                event.getChannel().sendMessageAsync("You are not allowed to manage roles. :thumbsdown::skin-tone-" + ((int)Math.ceil(Math.random() * 5)) + ":", null);
            else {
                logger.logCommandUse(event.getMessage());
                onCommand(new CommandEvent(event));
            }
        }
    }

    @Override
    public void onCommand(CommandEvent event) {
        if(!PermissionUtil.checkPermission(event.event.getJDA().getSelfInfo(), Permission.MANAGE_ROLES, event.event.getGuild())) {
            event.sendMessage("I am unable to manage roles. :thumbsdown::skin-tone-" + ((int)Math.ceil(Math.random() * 5)) + ":");
            return;
        }
        String[] args = event.allArguments.split("\\Q | \\E", 2);
        if(args.length < 1) {
            event.sendMessage("No role selected. Use the help command for more instructions.");
            return;
        }
        Role r = RoleUtil.getRoleByName(args[0], event.event.getGuild());
        if(r == null) {
            event.sendMessage("There is no role with the name \"" + event.allArguments + "\" "
                    + ":thumbsdown::skin-tone-" + ((int)Math.ceil(Math.random() * 5)) + ":\nType `" + prefix + "help " + prefix + "copyrole` for more information.");
            return;
        }
        try {
            if(args.length == 2)
                r = RoleUtil.copyRole(r, args[1]);
            else
                r = RoleUtil.copyRole(r, "");
            event.sendMessage("Copied role: `" + r.getName() + "` :thumbsup::skin-tone-" + ((int)Math.ceil(Math.random() * 5)) + ":");
        } catch (Exception e) {
            event.sendMessage("Something went wrong. Check error log. :thumbsdown::skin-tone-" + ((int)Math.ceil(Math.random() * 5)) + ":");
            logger.logThrowable(e);
        }
    }

    @Override
    public boolean isCommand(String message) {
        try {
            message = message.toLowerCase();
            if (!message.startsWith(prefix))
                return false;
            message = message.substring(prefix.length());
            String command = message.split(" ", 2)[0];
            if (command.equalsIgnoreCase("copyrole"))
                return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    public String usage() {
        return "`copyrole <rolename> | <copyrolename>` [example `copyrole OG Mods | Copy Mods`]";
    }

    @Override
    public String getAlias() {
        return "copyrole <rolename> | <copyrolename>";
    }

    @Override
    public String example() {
        return "copyrole Moderators | Admins";
    }

}