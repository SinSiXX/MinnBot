package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import net.dv8tion.jda.player.MusicPlayer;
import net.dv8tion.jda.player.Playlist;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;

import java.util.List;

public class QueueCommand extends CommandAdapter {

    public QueueCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.allArguments.isEmpty()) {
            event.sendMessage("You have to provide at least one URL.");
            return;
        }
        String[] urls = event.allArguments.replace(" ", "").split("\\Q,\\E");
        MusicPlayer player = MinnAudioManager.getPlayer(event.guild);
        final int[] count = {0};
        final int[] skipped = {0};
        for (String url : urls) {
            Playlist list;
            try {
                list = Playlist.getPlaylist(((url.startsWith("<") && url.endsWith(">")) ? url.substring(1, url.length() - 1) : url));
            } catch (NullPointerException ignored) {
                continue;
            }
            List<AudioSource> listSources = list.getSources();
            if (listSources.size() > 50) {
                event.sendMessage("Playlist contained more than 50 songs, skipping completely! RAM doesn't like you fam.");
                continue;
            } else if (listSources.size() > 1) {
                event.sendMessage("Detected Playlist! Starting to queue songs...");
            } else if(listSources.size() == 1) {
                event.sendMessage("Adding `" + listSources.get(0).getInfo().getTitle().replace("`", "\u0001`\u0001") + "` to the queue!");
            }

            listSources.parallelStream().filter(source -> {
                AudioInfo info = source.getInfo();
                if(info == null || info.getTitle() == null || info.getTitle().isEmpty()) {
                    event.sendMessage("A song is being skipped due to missing permissions by youtube!");
                    skipped[0]++;
                    return false;
                }
                return true;
            }).forEach(source -> {
                count[0]++;
                player.getAudioQueue().add(source);
            });
        }
        if (!player.isPlaying() && !player.getAudioQueue().isEmpty()) {
            player.play();
            event.sendMessage("Added provided URLs to the queue and the player started playing! " +
                    "**__Amount:__ " + count[0] + " __Skipped:__ "+ skipped[0]+"**");
            return;
        }
        event.sendMessage("Added provided URLs to the queue! " +
                "**__Amount:__ " + count[0] + " __Skipped:__ "+ skipped[0]+"**");
    }

    @Override
    public boolean isCommand(String message) {
        String[] p = message.split(" ", 2);
        return p.length > 0 && p[0].equalsIgnoreCase(prefix + "queue");
    }

    @Override
    public String getAlias() {
        return "queue <URL>, <URL>, <URL>, ...";
    }

    @Override
    public String example() {
        return "queue https://www.youtube.com/watch?v=58mah_0Y8TU , 58mah_0Y8TU,58mah_0Y8TU";
    }
}

