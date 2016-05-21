package minn.minnbot.entities.command.moderation;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.CommandManager;
import minn.minnbot.util.DeleteUtil;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class PurgeCommand extends CommandAdapter {

    public PurgeCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isPrivate())
            return;
        if (isCommand(event.getMessage().getContent(), CommandManager.getPrefixList(event.getGuild().getId()))) {
            if (!event.getTextChannel().checkPermission(event.getAuthor(), Permission.MESSAGE_MANAGE)) {
                return;
            } else if (!event.getTextChannel().checkPermission(event.getJDA().getSelfInfo(),
                    Permission.MESSAGE_MANAGE)) {
                event.getChannel()
                        .sendMessageAsync("I am unable to delete messages. Missing Permission: MESSAGE_MANAGE", null);
                return;
            }
            CommandEvent e = new CommandEvent(event, getAlias().split("\\s+")[0]);
            onCommand(e);
            logger.logCommandUse(event.getMessage(), this, e);
        }
    }

    @Override
    public void onCommand(CommandEvent event) {
        try {
            User u = event.message.getMentionedUsers().get(0);
            DeleteUtil.deleteFrom(((TextChannel) event.channel), e -> {
                if(e.isEmpty()) {
                    event.sendMessage(
                            u.getAsMention() + " has been purged by " + event.author.getUsername());
                    return;
                }
                event.sendMessage(String.format("**__ERROR:__ %s**", e.get(0).toString()));
            }, u);
        } catch (IndexOutOfBoundsException e) {
            event.sendMessage(String.format("I am unable to purge without mention reference. Usage: %s", usage()));
        }
    }

    @Override
    public String usage() {
        return "`purge @username`\t | Required Permissions: Manage Messages";
    }

    @Override
    public String getAlias() {
        return "purge <mention>";
    }

    @Override
    public String example() {
        return "purge <@158174948488118272>";
    }

}
