package minn.minnbot.entities.command.roles;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.CommandManager;
import minn.minnbot.util.RoleUtil;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.utils.PermissionUtil;

public class EditRoleCommand extends CommandAdapter {

     public EditRoleCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isPrivate())
            return;
        if (isCommand(event.getMessage().getContent(), CommandManager.getPrefixList(event.getGuild().getId()))) {
            if (!PermissionUtil.checkPermission(event.getAuthor(), Permission.MANAGE_ROLES, event.getGuild()))
                event.getChannel().sendMessageAsync("You are not allowed to manage roles. :thumbsdown::skin-tone-" + ((int) Math.ceil(Math.random() * 5)) + ":", null);
            else {
                CommandEvent e = new CommandEvent(event);
                onCommand(e);
                logger.logCommandUse(event.getMessage(), this, e);
            }
        }
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (!PermissionUtil.checkPermission(event.event.getJDA().getSelfInfo(), Permission.MANAGE_ROLES, event.event.getGuild())) {
            event.sendMessage("I am unable to manage roles. :thumbsdown::skin-tone-" + ((int) Math.ceil(Math.random() * 5)) + ":");
            return;
        }
        String[] args = event.allArguments.split("\\Q | \\E", 3);
        if (args.length < 3) {
            event.sendMessage("Syntax error. Use the help command for further instructions.");
            return;
        }
        Role r = RoleUtil.getRoleByName(args[0], event.event.getGuild());
        if (r == null) {
            event.sendMessage("There is no role with the name \"" + event.allArguments + "\" "
                    + ":thumbsdown::skin-tone-" + ((int) Math.ceil(Math.random() * 5)) + ":\nType `" + prefix + "help " + prefix + "copyrole` for more information.");
            return;
        }
        String method = args[1];
        if (method.equalsIgnoreCase("color")) {
            try {
                int maxC = 16777215; // HEX - FFFFFF
                int color = ((int) Math.floor(Math.random() * maxC)); // Random between 000000 - FFFFFF
                if (!args[2].equalsIgnoreCase("random")) {
                    try {
                        color = Integer.parseInt(args[2].replace("#", ""), 16); // In case the user wants a specific color instead.
                    } catch (NumberFormatException e) {
                        event.sendMessage("Invalid hex value `" + args[2] + "`. Example `" + Integer.toHexString(color) + "` or `#" + Integer.toHexString(color) + "`");
                        return;
                    }
                }
                r.getManager().setColor(color).update();
                event.sendMessage("Updated role color. :thumbsup::skin-tone-" + ((int) Math.ceil(Math.random() * 5)) + ":");
            } catch (Exception e) {
                logger.logThrowable(e);
                event.sendMessage("Something went wrong: " + e.getMessage());
            }
        } else if (method.equalsIgnoreCase("name")) {
            r.getManager().setName(args[2]).update();
            event.sendMessage("Updated role name to `" + args[2] + "`. :thumbsup::skin-tone-" + ((int) Math.ceil(Math.random() * 5)) + ":");
        } else {
            event.sendMessage("Invalid method `" + method + "`.");
        }
    }

    @Override
    public String usage() {
        return "`editrole <rolename> | <method> | <input>` (method[input]: `color[hex], name[string]`)";
    }

    @Override
    public String getAlias() {
        return "editrole <rolename> | <method> | <input>";
    }

    @Override
    public String example() {
        return "editrole Mods | color | 43FFC2";
    }

}
