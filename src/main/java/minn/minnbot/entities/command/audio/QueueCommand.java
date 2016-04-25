package minn.minnbot.entities.command.audio;

import com.sun.corba.se.impl.orbutil.threadpool.ThreadPoolImpl;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
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

    private ThreadPoolImpl pool;

    public QueueCommand(String prefix, Logger logger) {
        super.init(prefix, logger);
        pool = new ThreadPoolImpl("Player");
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.allArguments.isEmpty()) {
            event.sendMessage("You have to provide at least one URL.");
            return;
        }
        String[] urls = event.allArguments.replace(" ", "").split("\\Q,\\E");
        MusicPlayer player = MinnAudioManager.getPlayer(event.guild);
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
            } else if (listSources.size() == 1) {
                event.sendMessage("Adding `" + listSources.get(0).getInfo().getTitle().replace("`", "\u0001`\u0001") + "` to the queue!");
            }
            listSources.parallelStream().forEachOrdered(source -> {
                pool.getAnyWorkQueue().addWork(new WorkImpl(source, player.getAudioQueue(),event, player));
                /*AudioInfo info = source.getInfo();
                if (info == null) {
                    event.sendMessage("Source was not available. Skipping.");
                    skipped[0]++;
                    return;
                } else if (info.getError() != null) {
                    event.sendMessage("Error for source occurred: `" + info.getError() + "`.");
                    skipped[0]++;
                    return;
                }
                player.getAudioQueue().add(source);
                count[0]++; */
            });
        }
        /*if (!player.isPlaying() && !player.getAudioQueue().isEmpty()) {
            player.play();
            event.sendMessage("Added provided URLs to the queue and the player started playing! " +
                    "**__Amount:__ " + count[0] + " __Skipped:__ " + skipped[0] + "**");
            return;
        }
        event.sendMessage("Added provided URLs to the queue! " +
                "**__Amount:__ " + count[0] + " __Skipped:__ " + skipped[0] + "**");*/
    }


    private class WorkImpl implements Work {

        private AudioSource source;
        private List<AudioSource> sourceList;
        private CommandEvent event;
        private MusicPlayer player;

        WorkImpl(AudioSource source, List<AudioSource> sourceList, CommandEvent event, MusicPlayer player) {
            this.source = source;
            this.sourceList = sourceList;
            this.event = event;
            this.player = player;
        }

        @Override
        public void doWork() {
            AudioInfo info = source.getInfo();
            if (info == null) {
                event.sendMessage("Source was not available. Skipping.");
                return;
            } else if (info.getError() != null) {
                event.sendMessage("**__Error for source occurred:__** `" + info.getError() + "`.");
                return;
            }
            sourceList.add(source);
            if(!player.isPlaying()) {
                event.sendMessage("Enqueuing songs and starting playback...");
                player.play();
            }
        }

        @Override
        public void setEnqueueTime(long timeInMillis) {
            // TODO
        }

        @Override
        public long getEnqueueTime() {
            return 0;
        }

        @Override
        public String getName() {
            return "source-work";
        }
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

