package minn.minnbot.entities.command.moderation;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.EmoteUtil;
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
        if (event.isPrivate())
            return;
        super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        boolean userPerm = PermissionUtil.checkPermission(event.author, Permission.NICKNAME_MANAGE, event.guild);
        boolean botPerm = PermissionUtil.checkPermission(event.jda.getSelfInfo(), Permission.NICKNAME_MANAGE, event.guild);
        if (!userPerm) {
            event.sendMessage("You are not able to manage nicknames.");
            return;
        } else if (!botPerm) {
            event.sendMessage("I am not able to manage nicknames.");
            return;
        }
        List<User> users = event.event.getMessage().getMentionedUsers();

        if (users.isEmpty()) {
            User user = event.author;
            if (event.allArguments.length() > 20) {
                event.sendMessage("Nickname is too long, must be 0-20!");
                return;
            }
            event.guild.getManager().setNickname(user, event.allArguments);
            if (event.author.getUsername().equals(event.allArguments) || event.allArguments.isEmpty())
                event.sendMessage("Reset **your** nickname. " + EmoteUtil.getRngOkHand());
            else
                event.sendMessage("Updated **your** nickname: " + event.allArguments);
        } else {
            User user = users.get(0);
            String[] p = event.allArguments.split(" ", 2);
            String name;
            if (p.length >= 2) {
                name = p[1].trim();
            } else {
                name = "";
            }
            if (name.length() > 20) {
                event.sendMessage("Nickname is too long, must be 0-20!");
                return;
            }

            event.guild.getManager().setNickname(user, name);
            if (name.equals(user.getUsername()) || name.isEmpty())
                event.sendMessage("Users username has been reset. " + EmoteUtil.getRngOkHand());
            else
                event.sendMessage("Updated users nickname: " + name);
        }
    }

    @Override
    public boolean isCommand(String message) {
        String[] p = message.split(" ", 2);
        return p.length > 0 && p[0].equalsIgnoreCase(prefix + "nick");
    }

    @Override
    public String getAlias() {
        return "nick <mention> <name>";
    }

    public String usage() {
        return "\n**<mention>** - the user who's nickname you want to change\n**<name>** - the name to give, leave blank to reset.\n" +
                "\n**__Syntax:__** `nick <@mention> <name>`, `nick <name>`\n" +
                "\n**__Examples:__** \n" + prefix + example() + "\n" + prefix + "nick MyNick";
    }

    public String example() {
        return "nick <@158174948488118272> Bot";
    }
}
