package minn.minnbot.entities.command.moderation;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.utils.PermissionUtil;

import java.util.List;

public class NickerCommand extends CommandAdapter {

    public NickerCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.isPrivate())
            return;
        super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        boolean userPerm = PermissionUtil.checkPermission(event.author, Permission.NICKNAME_MANAGE, event.guild);
        boolean botPerm = PermissionUtil.checkPermission(event.jda.getSelfInfo(), Permission.NICKNAME_MANAGE, event.guild);
        if(!userPerm) {
            event.sendMessage("You are not able to manage nicknames.");
            return;
        } else if (!botPerm) {
            event.sendMessage("I am not able to manage nicknames.");
            return;
        }
        List<User> users = event.event.getMessage().getMentionedUsers();

        if(users.isEmpty()) {
            User user = event.author;
            if(event.allArguments.length() > 20) {
                event.sendMessage("Nickname is too long, must be 0-20!");
                return;
            }
            event.guild.getManager().setNickname(user, event.allArguments);
            event.sendMessage("Updated **your** nickname: " + event.guild.getNicknameForUser(user));
        } else {
            User user = users.get(0);
            String[] p = event.allArguments.split("\\Q|\\E",2);
            if(p.length < 2) {
                event.sendMessage("Missing argument. Example: `" + example() + "`");
                return;
            }
            String name = p[1];
            if(name.length() > 20) {
                event.sendMessage("Nickname is too long, must be 0-20!");
                return;
            }
            event.guild.getManager().setNickname(user, name);
            event.sendMessage("Updated users nickname: " + event.guild.getNicknameForUser(user));
        }
    }

    @Override
    public boolean isCommand(String message) {
        String[] p = message.split(" ",2);
        return p.length > 0 && p[0].equalsIgnoreCase(prefix + "nick");
    }

    @Override
    public String getAlias() {
        return "nick <mention> | <name>";
    }

    public String usage() {
        return "\n**<mention>** - the user who's nickname you want to change\n**<name>** - the name to give, leave blank to reset.";
    }

    public String example() {
        return "nick <@158174948488118272> | Bot";
    }
}
