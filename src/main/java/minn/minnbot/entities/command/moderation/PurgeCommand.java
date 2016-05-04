package minn.minnbot.entities.command.moderation;

import minn.minnbot.AsyncDelete;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Message;
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
        if (isCommand(event.getMessage().getContent())) {
            if (!event.getTextChannel().checkPermission(event.getAuthor(), Permission.MESSAGE_MANAGE)) {
                return;
            } else if (!event.getTextChannel().checkPermission(event.getJDA().getSelfInfo(),
                    Permission.MESSAGE_MANAGE)) {
                event.getChannel()
                        .sendMessageAsync("I am unable to delete messages. Missing Permission: MESSAGE_MANAGE", null);
                return;
            }
            logger.logCommandUse(event.getMessage());
            onCommand(new CommandEvent(event));
        }
    }

    @Override
    public void onCommand(CommandEvent event) { // TODO: Batch delete
        try {
            User u = event.message.getMentionedUsers().get(0);
            java.util.List<Message> hist = new net.dv8tion.jda.MessageHistory(event.channel).retrieve(100);
            hist.stream().filter(m -> m.getAuthor() == u).forEachOrdered(m -> AsyncDelete.deleteAsync(m, null));
            event.sendMessage(
                    u.getAsMention() + " has been purged by " + event.author.getUsername());
        } catch (IndexOutOfBoundsException e) {
            event.sendMessage("I am unable to purge without mention reference. Usage: " + usage());
        }
    }

    @Override
    public boolean isCommand(String message) {
        String[] parts = message.split(" ", 2);
        return parts.length > 0 && parts[0].equalsIgnoreCase(prefix + "purge");
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
