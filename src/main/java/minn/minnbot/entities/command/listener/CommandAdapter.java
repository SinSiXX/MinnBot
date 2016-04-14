package minn.minnbot.entities.command.listener;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class CommandAdapter extends ListenerAdapter implements Command {

    protected Logger logger;
    protected String prefix;

    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContent();
        if (isCommand(message)) {
            logger.logCommandUse(event.getMessage());
            onCommand(new CommandEvent(event));
        }
    }

    public void setLogger(Logger logger) {
        if (logger == null)
            throw new IllegalArgumentException("Logger can not be null");
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }

    public void onCommand(CommandEvent event) {
        logger.logError(new UnsupportedOperationException("Unimplemented method. onCommand()"));
    }

    public boolean isCommand(String message) {
        return false;
    }

    public String usage() {
        return "";
    }

    public String getAlias() {
        return "";
    }

    public boolean requiresOwner() {
        return false;
    }
}
