package minn.minnbot.entities.command.goofy;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

import java.net.URLEncoder;

public class GoogleCommand extends CommandAdapter {

    public GoogleCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.allArguments.isEmpty()) {
            event.sendMessage("Missing arguments. Usage: " + usage());
            return;
        }
        //noinspection deprecation
        event.sendMessage("http://lmgtfy.com/?q=" + URLEncoder.encode(event.allArguments));
    }

    public String usage() {
        return "lmgtfy <query> | Returns a let me google that for you link with the added query";
    }

    @Override
    public String getAlias() {
        return "lmgtfy <query>";
    }

    public String example() { return "lmgtfy how to make a discord bot"; }
}
