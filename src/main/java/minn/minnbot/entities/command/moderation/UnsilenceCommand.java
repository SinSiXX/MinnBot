package minn.minnbot.entities.command.moderation;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.CommandManager;
import minn.minnbot.util.EntityUtil;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.PermissionOverride;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.managers.PermissionOverrideManager;

import java.util.List;

public class UnsilenceCommand extends CommandAdapter {

    public UnsilenceCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isPrivate())
            return;
        String message = event.getMessage().getContent();
        if (isCommand(message, CommandManager.getPrefixList(event.getGuild().getId()))) {
            if (!event.getTextChannel().checkPermission(event.getAuthor(), Permission.MANAGE_PERMISSIONS)) {
                event.getChannel().sendMessageAsync(
                        "You are not able to use that command. Missing permission: `MANAGE_PERMISSIONS`", null);
                return;
            } else if (!event.getTextChannel().checkPermission(event.getJDA().getSelfInfo(),
                    Permission.MANAGE_PERMISSIONS)) {
                event.getChannel().sendMessageAsync(
                        "I am not able to complete that command. Missing permission: `MANAGE_PERMISSIONS`", null);
                return;
            }
            CommandEvent e = new CommandEvent(event, getAlias().split("\\s+")[0]);
            onCommand(e);
            logger.logCommandUse(event.getMessage(), this, e);
        }
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.allArguments.isEmpty()) {
            event.sendMessage("There is no user specified to be un-silenced.");
            return;
        }
        Thread t =
        new Thread(() -> {
            User u;
            List<User> mentions = event.message.getMentionedUsers();
            if (!mentions.isEmpty())
                u = mentions.get(0);
            else {
                List<User> userList = EntityUtil.getUsersByName(event.allArguments, event.event.getTextChannel());
                if (userList.isEmpty()) {
                    event.sendMessage("**None of the users that have access to this channel match that sequence!");
                    return;
                }
                if (userList.size() > 1) {
                    StringBuilder b = new StringBuilder("Multiple users in this channel fit the given sequence. Please be more specific!");
                    for (int i = 0; i < userList.size() && i < 6; i++) {
                        String nick = event.guild.getNicknameForUser(userList.get(i));
                        String match = event.allArguments.toLowerCase();
                        b.append("\n- ").append(
                                (nick != null && event.guild.getNicknameForUser(userList.get(i)).toLowerCase().contains(match))
                                        ? event.guild.getNicknameForUser(userList.get(i)).contains("**")
                                        ? event.guild.getNicknameForUser(userList.get(i))
                                        : event.guild.getNicknameForUser(userList.get(i)).toLowerCase().replace(match, String.format("**%s**", match))
                                        : userList.get(i).getUsername().contains("**")
                                        ? userList.get(i).getUsername()
                                        : userList.get(i).getUsername().toLowerCase().replace(match, String.format("**%s**", match)));
                    }
                    event.sendMessage(b.toString());
                    return;
                }
                u = userList.get(0);
            }
            PermissionOverride override = event.event.getTextChannel().getOverrideForUser(u);
            PermissionOverrideManager overrideManager;
            if (override != null)
                overrideManager = override.getManager();
            else
                overrideManager = event.event.getTextChannel().createPermissionOverride(u);
            overrideManager.grant(Permission.MESSAGE_WRITE).update();
            event.sendMessage(u.getAsMention() + " has been un-silenced.");
        });
        t.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) logger);
        t.setDaemon(true);
        t.setName("Un-Silence(Async)");
        t.start();
    }

    @Override
    public String usage() {
        return "`unsilence @username`\t | Required Permissions: Manage Permissions";
    }

    @Override
    public String getAlias() {
        return "unsilence <mention>";
    }

    @Override
    public String example() {
        return "unsilence <@158174948488118272>";
    }

}
