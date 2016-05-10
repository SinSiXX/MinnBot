package minn.minnbot.entities.command.moderation;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.CommandManager;
import minn.minnbot.util.Misc;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

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
            logger.logCommandUse(event.getMessage());
            onCommand(new CommandEvent(event));
        }
    }

    @Override
    public void onCommand(CommandEvent event) {
        int amount = 0;
        try {
            amount = Integer.parseInt(event.allArguments);
            if (amount < 1) {
                event.sendMessage("Unable to delete less than one message!");
                return;
            }
        } catch (NumberFormatException ignored) {
            Misc.deleteFrom((TextChannel) event.channel);
        } finally {
            Misc.deleteFrom((TextChannel) event.channel, amount);
        }
        if (amount != 0)
            event.sendMessage(String.format("%s deleted %d messages in this channel!", event.author.getAsMention(), amount));
        else
            event.sendMessage(String.format("%s cleared the room.", event.author.getAsMention()));
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
