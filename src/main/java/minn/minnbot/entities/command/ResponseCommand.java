package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class ResponseCommand extends CommandAdapter {

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
    public void onCommand(CommandEvent event) {
        try {
            long start = System.currentTimeMillis();
            event.event.getChannel().sendMessageAsync("__**Response Time:**__ ", (Message m) -> m.updateMessageAsync("__**Response Time:**__ " + (System.currentTimeMillis() - start) + "ms", null));
        } catch(Exception e) {
            logger.logThrowable(e);
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
    public String example() {
        return "test";
    }

}
