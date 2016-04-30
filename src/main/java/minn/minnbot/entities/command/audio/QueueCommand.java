package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.player.MusicPlayer;
import net.dv8tion.jda.player.Playlist;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class QueueCommand extends CommandAdapter {

    private ExecutorService executor;

    public QueueCommand(String prefix, Logger logger) {
        super.init(prefix, logger);
        executor = new ThreadPoolExecutor(1, 15, 3L, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), r -> {
            final Thread thread = new Thread(r, "QueueExecution-Thread");
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.setDaemon(true);
            thread.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) logger);
            return thread;
        });
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isPrivate())
            return;
        super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.allArguments.isEmpty()) {
            event.sendMessage("You have to provide at least one URL.");
            return;
        }
        event.sendMessage("Validating request, this may take a few minutes...", msg -> executor.execute(() -> {
            String[] urls = event.allArguments.trim().replace(" ", "").split("\\Q,\\E");
            MusicPlayer player = MinnAudioManager.getPlayer(event.guild);

            final boolean[] error = {false};
            for (String url : urls) {
                if(url.contains("https://gaming.youtube.com/watch?v=")) {
                    msg.updateMessageAsync("Youtube Gaming URLs are not accepted. Skipping...", null);
                    continue;
                }
                Playlist list;
                // get playlist
                try {
                    list = Playlist.getPlaylist(((url.startsWith("<") && url.endsWith(">")) ? url.substring(1, url.length() - 1) : url));
                } catch (NullPointerException ignored) {
                    continue;
                }
                List<AudioSource> listSources = list.getSources();
                if (listSources.size() > 1) {
                    msg.updateMessageAsync("Detected Playlist! Starting to queue songs...", null);
                } else if (listSources.size() == 1) {
                    AudioInfo audioInfo = listSources.get(0).getInfo();
                    if (audioInfo.getError() != null) {
                        msg.updateMessageAsync("**__Error with source:__ " + audioInfo.getError().trim() + "**", null);
                        continue;
                    }
                    msg.updateMessageAsync("Adding `" + audioInfo.getTitle().replace("`", "\u0001`\u0001") + "` to the queue!", null);
                } else {
                    msg.updateMessageAsync("Source had no attached/readable information. Skipping...", null);
                    continue;
                }
                // init executor
                /*ThreadPoolExecutor listExecutor = new ThreadPoolExecutor(1, 50, 1L, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), r -> {
                    final Thread thread = new Thread(r, "ListValidation-Thread");
                    thread.setPriority(Thread.MAX_PRIORITY);
                    thread.setDaemon(true);
                    thread.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) logger);
                    return thread;
                });*/
                // execute
                listSources.parallelStream().forEachOrdered(source -> /*listExecutor.execute(() ->*/ {
                    AudioInfo info = source.getInfo();
                    if (info == null) {
                        if (!error[0]) {
                            msg.updateMessageAsync("Source was not available. Skipping.", null);
                            error[0] = true;
                        }
                        return;
                    } else if (info.getError() != null) {
                        if (!error[0]) {
                            msg.updateMessageAsync("**One or more sources were not available. Sorry fam.**", null);
                            error[0] = true;
                        }
                        return;
                    }
                    if(info.getDuration().getFullTimestamp().equals("00:30:00.000")) {
                        msg.updateMessageAsync("Origin is detected as a live stream and will not be played.\nJoin the dev server linked in the info command to report a bug.", null);
                        return;
                    }
                    player.getAudioQueue().add(source);
                    if (!player.isPlaying()) {
                        msg.updateMessageAsync("Enqueuing songs and starting playback...", null);
                        player.play();
                    }
                }/*)*/);
            }
        }));
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

