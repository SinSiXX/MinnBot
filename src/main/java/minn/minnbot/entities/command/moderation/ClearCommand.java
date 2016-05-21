package minn.minnbot.entities.command.moderation;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.CommandManager;
import minn.minnbot.util.DeleteUtil;
import net.dv8tion.jda.MessageHistory;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.function.Consumer;

public class ClearCommand extends CommandAdapter {

    public ClearCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isPrivate())
            return;
        if (isCommand(event.getMessage().getContent(), CommandManager.getPrefixList(event.getGuild().getId()))) {
            if (!event.getTextChannel().checkPermission(event.getAuthor(), Permission.MESSAGE_MANAGE)) {
                return;
            } else if (!event.getTextChannel().checkPermission(event.getJDA().getSelfInfo(),
                    Permission.MESSAGE_MANAGE)) {
                if (event.getTextChannel().checkPermission(event.getJDA().getSelfInfo(), Permission.MESSAGE_WRITE))
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
        int amount[] = {0};
        Consumer<List<Exception>> callbaok = e -> {
            if (e.isEmpty()) {
                if (amount[0] != 99)
                    event.sendMessage(String.format("%s deleted %d messages in this channel!", event.author.getAsMention(), --amount[0]));
                else
                    event.sendMessage(String.format("%s cleared the room.", event.author.getAsMention()));
                return;
            }
            //noinspection ThrowableResultOfMethodCallIgnored
            event.sendMessage(String.format("**__ERROR:__ %s**", e.get(0).toString()));
        };
        try {
            amount[0] = Integer.parseInt(event.allArguments);
            if (amount[0] < 1) {
                event.sendMessage("Unable to delete less than one message!");
                return;
            }
        } catch (NumberFormatException ignored) {
            amount[0] = 98;
        }
        List<Message> messages = new MessageHistory(event.channel).retrieve(++amount[0]);
        messages.removeIf(message -> message.getId().equalsIgnoreCase(event.message.getId()));
        DeleteUtil.deleteIn(messages, (TextChannel) event.channel, callbaok);
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
