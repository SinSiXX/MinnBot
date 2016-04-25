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
       // pool = new ThreadPoolImpl(0, 2, 1000, "enqueue");
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
            if (listSources.size() > 1) {
                event.sendMessage("Detected Playlist! Starting to queue songs...");
            } else if (listSources.size() == 1) {
                event.sendMessage("Adding `" + listSources.get(0).getInfo().getTitle().replace("`", "\u0001`\u0001") + "` to the queue!");
            }
            listSources.parallelStream().forEachOrdered(source -> {
                /*Work work = new WorkImpl(source, player.getAudioQueue(), event, player);
                work.setEnqueueTime(System.currentTimeMillis());
                pool.getAnyWorkQueue().addWork(work);*/
                AudioInfo info = source.getInfo();
                if (info == null) {
                    event.sendMessage("Source was not available. Skipping.");
                    return;
                } else if (info.getError() != null) {
                    event.sendMessage("**__Error for source occurred:__** `" + info.getError() + "`");
                    return;
                }
                player.getAudioQueue().add(source);
                if (!player.isPlaying()) {
                    event.sendMessage("Enqueuing songs and starting playback...");
                    player.play();
                }
            });
        }
    }


    private class WorkImpl implements Work {

        private AudioSource source;
        private List<AudioSource> sourceList;
        private CommandEvent event;
        private MusicPlayer player;
        private long time = 5000;

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
            if (!player.isPlaying()) {
                event.sendMessage("Enqueuing songs and starting playback...");
                player.play();
            }
        }

        @Override
        public void setEnqueueTime(long timeInMillis) {
            time = timeInMillis;
        }

        @Override
        public long getEnqueueTime() {
            return time;
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

