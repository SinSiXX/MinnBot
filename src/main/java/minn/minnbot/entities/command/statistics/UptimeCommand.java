package minn.minnbot.entities.command.statistics;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.TimeUtil;

import java.net.UnknownHostException;
import java.util.List;

public class UptimeCommand extends CommandAdapter {


    public UptimeCommand(String prefix, Logger logger) throws UnknownHostException {
        this.prefix = prefix;
        this.logger = logger;
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
    public boolean isCommand(String message, List<String> prefixList) {
        String[] p = message.split(" ", 2);
        if(p.length < 1)
            return false;
        if(p[0].equalsIgnoreCase(prefix + "uptime"))
            return true;
        for(String fix : prefixList) {
            if(p[0].equalsIgnoreCase(fix + "uptime"))
                return true;
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
}
