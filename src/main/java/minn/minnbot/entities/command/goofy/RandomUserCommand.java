package minn.minnbot.entities.command.goofy;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

import java.util.List;
import java.util.Random;

public class RandomUserCommand extends CommandAdapter {

    public RandomUserCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        String user = event.jda.getUsers().get(new Random().nextInt(event.jda.getUsers().size())).getUsername();
        event.sendMessage("**__Random User:__ " + user + "**");
    }

    @Override
    public boolean isCommand(String message, List<String> prefixList) {
        String[] p = message.split(" ", 2);
        if(p.length < 1)
            return false;
        if(p[0].equalsIgnoreCase(prefix + "user"))
            return true;
        for(String fix : prefixList) {
            if(p[0].equalsIgnoreCase(fix + "user"))
                return true;
        }
        return false;
    }

    @Override
    public String getAlias() {
        return "user";
    }
}
