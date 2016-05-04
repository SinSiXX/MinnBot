package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.player.Playlist;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;

import java.util.List;

public class AudioInfoCommand extends CommandAdapter {

    public AudioInfoCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        Playlist playlist = Playlist.getPlaylist(event.allArguments);
        List<AudioSource> sourceList = playlist.getSources();
        if (sourceList.isEmpty()) {
            event.sendMessage("No info to get.");
            return;
        }
        sourceList.stream().forEach((source -> {
            AudioInfo info = source.getInfo();
            if (info.getError() != null) {
                event.sendMessage("**__Error:__** " + info.getError());
                return;
            }
            String title = info.getTitle();
            String duration = info.getDuration().getFullTimestamp();
            String extractor = info.getExtractor();
            String origin = info.getOrigin();
            event.sendMessage(title + "\n" + duration + "\n" + extractor + "\n" + origin);
        }));

    }

    @Override
    public boolean isCommand(String message) {
        String[] p = message.split(" ", 2);
        return p.length > 0 && p[0].equalsIgnoreCase(prefix + "getinfo");
    }

    @Override
    public String getAlias() {
        return "getinfo <url>";
    }

    public boolean requiresOwner() {
        return true;
    }
}
