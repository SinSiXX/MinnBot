package minn.minnbot.entities.command.statistics;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.TimeUtil;

import java.net.UnknownHostException;

public class UptimeCommand extends CommandAdapter {


    public UptimeCommand(String prefix, Logger logger) throws UnknownHostException {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        try {
            long[] nums = logger.getNumbers();
            event.sendMessage("**__Uptime:__" + TimeUtil.uptime(nums[5]) + "**");
        } catch (Exception e) {
            logger.logThrowable(e);
        }
    }

    @Override
    public String usage() {
        return "Returns uptime. duh";
    }

    @Override
    public String getAlias() {
        return "uptime";
    }
}
