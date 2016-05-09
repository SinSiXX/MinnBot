package minn.minnbot.entities.command.listener;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.CommandManager;
import minn.minnbot.util.CommandUtil;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

import java.util.List;

public abstract class CommandAdapter extends ListenerAdapter implements Command {

    protected Logger logger;
    protected String prefix;

    protected void init(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getRawContent();
        if (isCommand(message, CommandManager.getPrefixList(event.getGuild().getId()))) {
            logger.logCommandUse(event.getMessage());
            onCommand(new CommandEvent(event));
        }
    }

    public final Logger getLogger() {
        return logger;
    }

    public final void setLogger(Logger logger) {
        if (logger == null)
            throw new IllegalArgumentException("Logger can not be null");
        this.logger = logger;
    }

    public abstract void onCommand(CommandEvent event);

    @Override
    public boolean isCommand(String message, List<String> prefixList) {
        return CommandUtil.isCommand(prefix, getAlias().split("\\s+")[0], message) || CommandUtil.isCommand(prefixList, getAlias().split("\\s+")[0], message);
    }

    public String usage() {
        return "";
    }

    public abstract String getAlias();

    public String example() {
        return getAlias();
    }

    public boolean requiresOwner() {
        return false;
    }

    public String toString() {
        return "C:" + prefix + getAlias() + "(" + logger + ")";
    }

}
