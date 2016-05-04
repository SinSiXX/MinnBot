package minn.minnbot.entities.command.goofy;

import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.MemeUtil;

public class MemeCommand extends CommandAdapter {

    public MemeCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    @Override
    public void onCommand(CommandEvent event) {
        if(event.arguments.length < 1) {
            event.sendMessage("Missing arguments. Usage: " + usage());
            return;
        }
        String template = event.arguments[0];
        String[] parts = event.allArguments.split(" ",2);
        if(parts.length < 2) {
            event.sendMessage("Missing arguments. Usage: " + usage());
            return;
        }
        parts = parts[1].split("\\Q|\\E",2);
        if(parts.length == 1) {
            try {
                if(parts[0].isEmpty()) {
                    parts[0] = "_";
                }
                event.sendMessage(MemeUtil.generateMeme(template, parts[0], "_"));
            } catch (UnirestException e) {
                logger.logThrowable(e);
            }
        } else if(parts.length != 0) {
            try {
                if(parts[0].isEmpty()) {
                    parts[0] = "_";
                }
                if(parts[1].isEmpty()) {
                    parts[1] = "_";
                }
                event.sendMessage(MemeUtil.generateMeme(template, parts[0], parts[1]));
            } catch (UnirestException e) {
                logger.logThrowable(e);
            }
        } else {
            event.sendMessage(usage());
        }
    }

    @Override
    public boolean isCommand(String message) {
        try {
            String cmd = message.split(" ",2)[0];
            return cmd.equalsIgnoreCase(prefix + "meme");
        } catch (IndexOutOfBoundsException ignored) {}
        return false;
    }

    public String usage() {
        return "`meme <template> <text> | <text>` Templates: http://memegen.link/templates\nExample: `meme fry not sure if clear | or not`";
    }

    @Override
    public String getAlias() {
        return "meme <template> <text> | <text>";
    }

    @Override
    public String example() {
        return "meme doge Such Example | Much helpful";
    }

}
