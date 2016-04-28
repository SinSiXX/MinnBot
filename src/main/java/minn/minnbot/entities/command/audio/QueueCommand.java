package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.player.MusicPlayer;
import net.dv8tion.jda.player.Playlist;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;

import java.rmi.UnexpectedException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class QueueCommand extends CommandAdapter {

    private ThreadPoolExecutor executor;

    public QueueCommand(String prefix, Logger logger) {
        super.init(prefix, logger);
        executor = new ThreadPoolExecutor(1, 5, 3L, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), r -> {
            final Thread thread = new Thread(r, "QueueExecution-Thread");
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.setDaemon(true);
            thread.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) logger);
            return thread;
        });
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.isPrivate())
            return;
        super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.allArguments.isEmpty()) {
            event.sendMessage("You have to provide at least one URL.");
            return;
        }
        List<Message> tmp = new LinkedList<>();
        event.sendMessage("Validating request, this may take a few minutes...", tmp::add);
        int counting = 0;
        while(tmp.size() < 1) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
            if(++counting == 25) {
                logger.logThrowable(new UnexpectedException("[QueueCommand] Callback was never called."));
                return;
            }
        }
        executor.execute(() -> {
            String[] urls = event.allArguments.trim().replace(" ", "").split("\\Q,\\E");
            MusicPlayer player = MinnAudioManager.getPlayer(event.guild);

            final boolean[] error = {false};
            for (String url : urls) {
                Playlist list;
                // get playlist
                try {
                    list = Playlist.getPlaylist(((url.startsWith("<") && url.endsWith(">")) ? url.substring(1, url.length() - 1) : url));
                } catch (NullPointerException ignored) {
                    continue;
                }
                List<AudioSource> listSources = list.getSources();
                if (listSources.size() > 1) {
                    tmp.get(0).updateMessageAsync("Detected Playlist! Starting to queue songs...", (message -> tmp.set(0, message)));
                } else if (listSources.size() == 1) {
                    AudioInfo audioInfo = listSources.get(0).getInfo();
                    if(audioInfo.getError() != null) {
                        tmp.get(0).updateMessageAsync("**__Error with source:__ " + audioInfo.getError().trim() + "**", (message -> tmp.set(0, message)));
                        continue;
                    }
                    tmp.get(0).updateMessageAsync("Adding `" + audioInfo.getTitle().replace("`", "\u0001`\u0001") + "` to the queue!", (message -> tmp.set(0, message)));
                }
                // init executor
                ThreadPoolExecutor listExecutor = new ThreadPoolExecutor(1, 50, 1L, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), r -> {
                    final Thread thread = new Thread(r, "ListValidation-Thread");
                    thread.setPriority(Thread.MAX_PRIORITY);
                    thread.setDaemon(true);
                    thread.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) logger);
                    return thread;
                });
                // execute
                listSources.parallelStream().forEachOrdered(source -> listExecutor.execute(() -> {
                    AudioInfo info = source.getInfo();
                    if (info == null) {
                        if (!error[0]) {
                            tmp.get(0).updateMessageAsync("Source was not available. Skipping.", (message -> tmp.set(0, message)));
                            error[0] = true;
                        }
                        return;
                    } else if (info.getError() != null) {
                        if (!error[0]) {
                            tmp.get(0).updateMessageAsync("**One or more sources were not available. Sorry fam.**", (message -> tmp.set(0, message)));
                            error[0] = true;
                        }
                        return;
                    }
                    player.getAudioQueue().add(source);
                    if (!player.isPlaying()) {
                        tmp.get(0).updateMessageAsync("Enqueuing songs and starting playback...", (message -> tmp.set(0, message)));
                        player.play();
                    }
                }));
            }
        });
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

