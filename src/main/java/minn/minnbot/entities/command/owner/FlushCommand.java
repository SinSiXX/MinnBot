package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.User;

import java.util.List;

import static minn.minnbot.AsyncDelete.deleteAsync;

public class FlushCommand extends CommandAdapter {

    public FlushCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    @Override
    public void onCommand(CommandEvent event) {
        User u = event.event.getJDA().getSelfInfo();
        Thread t = new Thread(() -> {
            java.util.List<Message> hist = new net.dv8tion.jda.MessageHistory(event.event.getChannel()).retrieve(100);
            hist.stream().filter(m -> m.getAuthor() == u).forEachOrdered(m -> deleteAsync(m, null));
        });
        t.start();
    }

    @Override
    public boolean isCommand(String message, List<String> prefixList) {
        String[] p = message.split(" ", 2);
        if(p.length < 1)
            return false;
        if(p[0].equalsIgnoreCase(prefix + "flush"))
            return true;
        for(String fix : prefixList) {
            if(p[0].equalsIgnoreCase(fix + "flush"))
                return true;
        }
        return false;
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
