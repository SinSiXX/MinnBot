package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.audio.MinnPlayer;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

import java.io.File;
import java.util.List;

public class CurrentCommand extends CommandAdapter{

    private MinnPlayer player;

    public CurrentCommand(String prefix, Logger logger, MinnPlayer player) {
        this.player = player;
        this.logger = logger;
        this.prefix = prefix;
    }

    @Override
    public void onCommand(CommandEvent event) {
        if(player == null) {
            event.sendMessage("No player available.");
            return;
        }
        List<File> playlist = player.getPlaylist();
        File previous = player.getPrevious();
        File current = player.getCurrent();
        String s = "";
        if(previous != null) {
            s += "**__Previously:__** `" + (previous.getName().substring(0, previous.getName().length() - ".mp3".length())).replace("```", "").replace("_"," ") + "`\n";
        }
        if(!player.isStopped() && current != null) {
            s += "**__Currently:__** `" + (current.getName().substring(0, current.getName().length() - ".mp3".length())).replace("`", "").replace("_"," ") + "`\n";
        }
        if(playlist.isEmpty()) {
            s += "**__No queued songs.__**";
        } else {
            int index = 1;
            s += "**__Queue:__**\n";
            for(File f : playlist) {
                if(index > 5) {
                    s += "**...**";
                    break;
                }
                s += "\t**" + index++ + ")** `" + (f.getName().substring(0, f.getName().length() - ".mp3".length())).replace("`", "").replace("_"," ") + "`\n";
            }
        }
        event.sendMessage(s);
    }

    @Override
    public boolean isCommand(String message) {
        String[] parts = message.split(" ", 2);
        return parts.length > 0 && parts[0].equalsIgnoreCase(prefix + "current");
    }

    @Override
    public String getAlias() {
        return "current";
    }
}
