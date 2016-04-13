package minn.minnbot.entities.command;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.EmoteUtil;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.utils.PermissionUtil;

public class SoftbanCommand extends ListenerAdapter implements Command {

    private String prefix;
    private Logger logger;

    public SoftbanCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isPrivate())
            return;
        if (isCommand(event.getMessage().getContent())) {
            if (!PermissionUtil.checkPermission(event.getAuthor(), Permission.BAN_MEMBERS, event.getGuild())) {
                return;
            } else if (!PermissionUtil.checkPermission(event.getJDA().getSelfInfo(),
                    Permission.BAN_MEMBERS, event.getGuild())) {
                event.getChannel()
                        .sendMessageAsync("I am unable to ban members. Missing Permission: BAN_MEMBERS", null);
                return;
            }
            logger.logCommandUse(event.getMessage());
            onCommand(new CommandEvent(event));
        }
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void setLogger(Logger logger) {
        if (logger == null)
            throw new IllegalArgumentException("Logger can not be null.");
        this.logger = logger;
    }

    @Override
    public void onCommand(CommandEvent event) {
        try {
            User target = event.event.getMessage().getMentionedUsers().get(0);
            event.event.getGuild().getManager().ban(target, 7);
            try{Thread.sleep(1000);} catch (InterruptedException ignored){}
            event.event.getGuild().getManager().unBan(target);
            event.sendMessage("Softbanned `" + target.getUsername() + "`. " + EmoteUtil.getRngThumbsup());
        } catch (IndexOutOfBoundsException e) {
           event.sendMessage("Unablet to softban without mention. " + EmoteUtil.getRngThumbsdowns());
        } catch (Exception e) {
            logger.logError(e);
        }
    }

    @Override
    public boolean isCommand(String message) {
        String[] parts = message.split(" ", 2);
        return parts.length >= 1 && (parts[0].equalsIgnoreCase(prefix + "softban") || parts[0].equalsIgnoreCase(prefix + "sb"));
    }

    @Override
    public String usage() {
        return "`softban @username` or `sb @username`\t | Required Permissions: Ban Members";
    }

    @Override
    public String getAlias() {
        return "softban <mention>";
    }

    @Override
    public boolean requiresOwner() {
        return false;
    }

}
