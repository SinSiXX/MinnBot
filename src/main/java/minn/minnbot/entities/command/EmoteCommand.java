package minn.minnbot.entities.command;

import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.TwitchUtil;

import java.io.File;
import java.io.IOException;

public class EmoteCommand extends CommandAdapter {

    public EmoteCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        try {
            TwitchUtil.Emote e = TwitchUtil.Emote.findFirstContaining(event.allArguments);
            if (e == null) {
                event.sendMessage("No twitch emote containing **" + event.allArguments + "** found!");
                return;
            }
            File f = new File("Emote.png");
            f.createNewFile();
            if (e.download(f))
                event.sendFile(f, String.format("**%s**", e.CODE));
            else
                event.sendMessage("I was unable to download that emote!");
        } catch (UnirestException | IOException e) {
            event.sendMessage("**" + e.getMessage() + "**");
        }
    }

    @Override
    public String getAlias() {
        return "emote [grouping] <code>";
    }

    public String usage() {
        return "\n**" + prefix + "emote [grouping] <code>\ngrouping - global,sub - (optional) looking for global or sub emote matching <code>\ncode - emote code (like `Kappa`)**";
    }

    public String example() {
        return "emote global Kappa";
    }
}
