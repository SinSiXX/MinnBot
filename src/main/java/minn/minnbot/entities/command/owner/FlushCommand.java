package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.DeleteUtil;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.TextChannel;

public class FlushCommand extends CommandAdapter {

    private String owner;

    public FlushCommand(String prefix, Logger logger, String owner) {
        init(prefix, logger);
        this.owner = owner;
    }

    @Override
    public void onCommand(CommandEvent event) {

        if (!event.isPrivate) {
            if (!event.event.getTextChannel().checkPermission(event.author, Permission.MESSAGE_MANAGE) && !event.author.getId().equals(owner)) {
                event.sendMessage("You are not allowed to delte my messages!");
                return;
            } else if(!event.event.getTextChannel().checkPermission(event.jda.getSelfInfo(), Permission.MESSAGE_MANAGE)){
                event.sendMessage("I am unable to delete my messages.");
                return;
            }
        }
        DeleteUtil.deleteFrom(((TextChannel) event.channel), e -> {
            if (e.isEmpty())
                return;
            event.sendMessage(String.format("**__ERROR:__ %s**", e.get(0).toString()));
        }, event.jda.getSelfInfo());
    }

    @Override
    public String getAlias() {
        return "flush";
    }

    @Override
    public boolean requiresOwner() {
        return false;
    }

}
