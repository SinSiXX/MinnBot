package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.Message;

public class PingCommand extends CommandAdapter {

    public PingCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    @Override
    public void onCommand(CommandEvent event) {
        try {
            long ping = System.currentTimeMillis();
            event.event.getChannel().sendMessageAsync("**__Ping:__** ", (Message m) -> m.updateMessageAsync("**__Ping:__** **" + (System.currentTimeMillis() - ping) + "ms**", null));
        } catch (Exception e) {
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
            if (command.equalsIgnoreCase("ping"))
                return true;
        } catch (Exception ignore) {
        }
        return false;
    }

    @Override
    public String usage() {
        return "Returns ping.";
    }

    @Override
    public String getAlias() {
        return "ping";
    }

}
