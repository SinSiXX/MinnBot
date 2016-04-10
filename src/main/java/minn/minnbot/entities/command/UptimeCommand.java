package minn.minnbot.entities.command;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.TimeUtil;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

import java.net.*;

public class UptimeCommand extends ListenerAdapter implements Command {

    private String prefix;
    private Logger logger;
    private InetAddress address;

    public UptimeCommand(String prefix, Logger logger) throws UnknownHostException {
        this.prefix = prefix;
        this.logger = logger;
        try {
            this.address = InetAddress.getByName("discordapp.com");
        } catch (UnknownHostException e) {
            logger.logError(e);
            throw e;
        }
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
            int[] nums = logger.getNumbers();
            event.sendMessage("**__Uptime:__** ***" + TimeUtil.uptime(nums[5]) + "***");
        } catch (Exception e) {
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
            if (command.equalsIgnoreCase("uptime"))
                return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public String usage() {
        return "Returns uptime. duh";
    }

    @Override
    public String getAlias() {
        return "uptime";
    }

    @Override
    public boolean requiresOwner() {
        return false;
    }


}
