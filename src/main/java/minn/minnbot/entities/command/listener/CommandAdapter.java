package minn.minnbot.entities.command.listener;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public abstract class CommandAdapter extends ListenerAdapter implements Command {

    protected Logger logger;
    protected String prefix;

    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContent();
        if (isCommand(message)) {
            logger.logCommandUse(event.getMessage());
            onCommand(new CommandEvent(event));
        }
    }

    public final void setLogger(Logger logger) {
        if (logger == null)
            throw new IllegalArgumentException("Logger can not be null");
        this.logger = logger;
    }

    public final Logger getLogger() {
        return logger;
    }

    public abstract void onCommand(CommandEvent event);

    public abstract boolean isCommand(String message);

    public String usage() { return ""; }

    public abstract String getAlias();

    public boolean requiresOwner() {
        return false;
    }
}
