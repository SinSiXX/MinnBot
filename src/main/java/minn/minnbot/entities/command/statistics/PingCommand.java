package minn.minnbot.entities.command.statistics;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.Message;

import java.util.List;

public class PingCommand extends CommandAdapter {

    public PingCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    @Override
    public void onCommand(CommandEvent event) {
        long ping = System.currentTimeMillis();
        event.channel.sendMessageAsync("**__Ping:__** ", (Message m) -> m.updateMessageAsync("**__Ping:__** **" + (System.currentTimeMillis() - ping) + "ms**", null));
    }

    @Override
    public boolean isCommand(String message, List<String> prefixList) {
        String[] p = message.split(" ", 2);
        if(p.length < 1)
            return false;
        if(p[0].equalsIgnoreCase(prefix + "ping"))
            return true;
        for(String fix : prefixList) {
            if(p[0].equalsIgnoreCase(fix + "ping"))
                return true;
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

    @Override
    public String example() {
        return "ping";
    }

}
