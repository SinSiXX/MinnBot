package minn.minnbot.entities.command.moderation;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.CommandManager;
import minn.minnbot.util.EmoteUtil;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.exceptions.PermissionException;
import net.dv8tion.jda.exceptions.VerificationLevelException;
import net.dv8tion.jda.utils.PermissionUtil;

public class SoftbanCommand extends CommandAdapter {

    public SoftbanCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isPrivate())
            return;
        if (isCommand(event.getMessage().getContent(), CommandManager.getPrefixList(event.getGuild().getId()))) {
            try {
                if (!PermissionUtil.checkPermission(event.getAuthor(), Permission.BAN_MEMBERS, event.getGuild())) {
                    event.getChannel().sendMessageAsync("You are not authorized to use this command. Missing Permission: **BAN_MEMBERS**", null);
                    return;
                } else if (!PermissionUtil.checkPermission(event.getJDA().getSelfInfo(),
                        Permission.BAN_MEMBERS, event.getGuild())) {

                    event.getChannel()
                            .sendMessageAsync("I am unable to ban members. Missing Permission: **BAN_MEMBERS**", null);
                    return;
                }
            } catch (PermissionException | VerificationLevelException ignored) {
            }
            CommandEvent e = new CommandEvent(event, getAlias().split("\\s+")[0]);
            onCommand(e);
            logger.logCommandUse(event.getMessage(), this, e);
        }
    }

    @Override
    public void onCommand(CommandEvent event) {
        try {
            User target = event.event.getMessage().getMentionedUsers().get(0);
            if (!PermissionUtil.canInteract(event.jda.getSelfInfo(), target, event.guild)) {
                event.sendMessage(String.format("I am unable to ban **%s**.%s", target.getUsername(), EmoteUtil.getRngOkHand()));
                return;
            }
            event.event.getGuild().getManager().ban(target, 7);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            event.event.getGuild().getManager().unBan(target);
            event.sendMessage("Softbanned `" + target.getUsername() + "`. " + EmoteUtil.getRngThumbsup());
        } catch (IndexOutOfBoundsException e) {
            event.sendMessage("Unablet to softban without mention. " + EmoteUtil.getRngThumbsdown());
        }
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
    public String example() {
        return "softban <@158174948488118272>";
    }
}
