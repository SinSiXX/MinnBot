package minn.minnbot.entities.command;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class ResponseCommand extends ListenerAdapter implements Command {

    private String prefix;
    private Logger logger;

    public ResponseCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (isCommand(event.getMessage().getContent())) {
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
            throw new IllegalArgumentException("Logger cannot be null");
        this.logger = logger;
    }

    @Override
    public void onCommand(CommandEvent event) {
        try {
            long start = System.currentTimeMillis();
            event.event.getChannel().sendMessageAsync("__**Response Time:**__ ", (Message m) -> m.updateMessage("__**Response Time:**__ " + (System.currentTimeMillis() - start) + "ms"));
        } catch(Exception e) {
            logger.logError(e);
        }
    }

    @Override
    public boolean isCommand(String message) {
        try {
            message = message.toLowerCase();
            if (!message.startsWith(prefix))
                return false;
            message = message.substring(prefix.length());
            String command = message.split(" ", 2)[0];
            if (command.equalsIgnoreCase("test"))
                return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public String usage() {
        return "Returns response time. (Not ping)";
    }

    @Override
    public String getAlias() {
        return "test";
    }

    @Override
    public boolean requiresOwner() {
        return false;
    }


}
