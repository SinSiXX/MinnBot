package minn.minnbot.entities.command.roles;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.CommandManager;
import minn.minnbot.util.EmoteUtil;
import minn.minnbot.util.RoleUtil;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.utils.PermissionUtil;

public class CopyRoleCommand extends CommandAdapter {

    public CopyRoleCommand(Logger logger, String prefix) {
        init(prefix, logger);
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isPrivate())
            return;
        if (isCommand(event.getMessage().getContent(), CommandManager.getPrefixList(event.getGuild().getId()))) {
            if (!PermissionUtil.checkPermission(event.getAuthor(), Permission.MANAGE_ROLES, event.getGuild()))
                event.getChannel().sendMessageAsync("You are not allowed to manage roles. " + EmoteUtil.getRngThumbsdown(), null);
            else {
                logger.logCommandUse(event.getMessage());
                onCommand(new CommandEvent(event));
            }
        }
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (!PermissionUtil.checkPermission(event.event.getJDA().getSelfInfo(), Permission.MANAGE_ROLES, event.event.getGuild())) {
            event.sendMessage("I am unable to manage roles. " + EmoteUtil.getRngThumbsdown());
            return;
        }
        String[] args = event.allArguments.split("\\Q | \\E", 2);
        if (args.length < 1) {
            event.sendMessage("No role selected. Use the help command for more instructions.");
            return;
        }
        Role r = RoleUtil.getRoleByName(args[0], event.event.getGuild());
        if (r == null) {
            event.sendMessage("There is no role with the name \"" + event.allArguments + "\" "
                    + EmoteUtil.getRngThumbsdown() + ":\nType `" + prefix + "help " + prefix + "copyrole` for more information.");
            return;
        }
        if (args.length == 2)
            r = RoleUtil.copyRole(r, args[1]);
        else
            r = RoleUtil.copyRole(r, "");
        event.sendMessage("Copied role: `" + r.getName() + "` " + EmoteUtil.getRngOkHand());
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