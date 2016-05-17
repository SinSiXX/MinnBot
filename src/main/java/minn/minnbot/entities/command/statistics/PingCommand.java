package minn.minnbot.entities.command.statistics;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

public class PingCommand extends CommandAdapter {

    public PingCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    @Override
    public void onCommand(CommandEvent event) {
        long ping = System.currentTimeMillis();
        event.sendMessage("**__Ping:__** ", m -> {
            if(m != null) m.updateMessageAsync("**__Ping:__** **" + (System.currentTimeMillis() - ping) + "ms**", null);
        });
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
