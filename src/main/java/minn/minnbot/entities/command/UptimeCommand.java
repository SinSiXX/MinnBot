package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.TimeUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class UptimeCommand extends CommandAdapter {

    private InetAddress address;

    public UptimeCommand(String prefix, Logger logger) throws UnknownHostException {
        this.prefix = prefix;
        this.logger = logger;
        try {
            this.address = InetAddress.getByName("discordapp.com");
        } catch (UnknownHostException e) {
            logger.logThrowable(e);
            throw e;
        }
    }

    @Override
    public void onCommand(CommandEvent event) {
        try {
            int[] nums = logger.getNumbers();
            event.sendMessage("**__Uptime:__** ***" + TimeUtil.uptime(nums[5]) + "***");
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
            if (command.equalsIgnoreCase("uptime"))
                return true;
        } catch (Exception ignored) {
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
    public String example() {
        return getAlias();
    }
}
