package minn.minnbot.entities.command.goofy;

import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.MemeUtil;

public class MemeCommand extends CommandAdapter {

    public MemeCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.allArguments.isEmpty()) {
            event.sendMessage("Missing arguments. Usage: " + usage());
            return;
        }
        String template = event.arguments[0];
        String[] parts = event.allArguments.split("\\s+", 2);
        try {
            if (parts.length < 2) {
                event.sendMessage(MemeUtil.generateMeme(template, "_", "_"));
                return;
            }
            parts = parts[1].split("\\s*[|]\\s*", 2);
            if (parts.length == 1) {
                if (parts[0].isEmpty()) {
                    parts[0] = "_";
                }
                event.sendMessage(MemeUtil.generateMeme(template, parts[0], "_"));
            } else {
                if (parts.length == 2) {
                    if (parts[0].isEmpty()) {
                        parts[0] = "_";
                    }
                    if (parts[1].isEmpty()) {
                        parts[1] = "_";
                    }
                    event.sendMessage(MemeUtil.generateMeme(template, parts[0], parts[1]));
                } else event.sendMessage(MemeUtil.generateMeme(template, "_", "_"));
            }
        } catch (UnirestException e) {
            logger.logThrowable(e);
        }
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
