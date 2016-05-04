package minn.minnbot.entities.command.moderation;

import minn.minnbot.AsyncDelete;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.MessageHistory;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class ClearCommand extends CommandAdapter {

    public ClearCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isPrivate())
            return;
        if (isCommand(event.getMessage().getContent())) {
            if (!event.getTextChannel().checkPermission(event.getAuthor(), Permission.MESSAGE_MANAGE)) {
                return;
            } else if (!event.getTextChannel().checkPermission(event.getJDA().getSelfInfo(),
                    Permission.MESSAGE_MANAGE)) {
                if (event.getTextChannel().checkPermission(event.getJDA().getSelfInfo(), Permission.MESSAGE_WRITE))
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
        int amount = 100;
        final int[] count = {0};
        try {
            amount = Integer.parseInt(event.allArguments);
        } catch (NumberFormatException ignored) {
        }
        java.util.List<Message> hist = new MessageHistory(event.event.getTextChannel()).retrieve(amount);
        event.sendMessage(event.author.getAsMention() + ", cleared messages in this channel!");
        hist.parallelStream().forEachOrdered(m -> AsyncDelete.deleteAsync(m, (Object) -> count[0]++));
        // event.sendMessage(event.event.getAuthor().getAsMention() + ", deleted " + count[0] + " messages in this channel.");
    }

    @Override
    public boolean isCommand(String message) {
        String[] parts = message.split(" ", 2);
        return parts.length > 0 && parts[0].equalsIgnoreCase(prefix + "clear");
    }

    @Override
    public String usage() {
        return "`clear <amount>` or `clear`\t | Required Permissions: Manage Messages";
    }

    @Override
    public String getAlias() {
        return "clear <amount>";
    }

    @Override
    public String example() {
        return "clear 50";
    }

}
