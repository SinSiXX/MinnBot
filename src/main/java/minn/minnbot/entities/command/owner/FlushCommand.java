package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.Misc;
import net.dv8tion.jda.entities.TextChannel;

public class FlushCommand extends CommandAdapter {

    public FlushCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    @Override
    public void onCommand(CommandEvent event) {
        Misc.deleteFrom(((TextChannel) event.channel), event.jda.getSelfInfo());
    }

    @Override
    public String getAlias() {
        return "flush";
    }

    @Override
    public boolean requiresOwner() {
        return true;
    }

}
