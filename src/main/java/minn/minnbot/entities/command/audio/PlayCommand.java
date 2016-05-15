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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PlayCommand extends CommandAdapter {

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 25, 10L, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), r -> {
        final Thread thread = new Thread(r, "QueueExecution-Thread");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setDaemon(true);
        return thread;
    });
    private Map<String, Long> limitation = new HashMap<>();

    public PlayCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isPrivate())
            return;
        super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        if(limitation.containsKey(event.guild.getId())) {
            long time = limitation.get(event.guild.getId());
            long retry_after = time - System.currentTimeMillis();
            if(retry_after > 0) {
                long seconds = TimeUnit.MILLISECONDS.toSeconds(retry_after);
                if(seconds > 60)
                    event.sendMessage(String.format("**You are being rate limited! Retry after: %d Minutes!**", TimeUnit.SECONDS.toMinutes(seconds)));
                else
                    event.sendMessage(String.format("**You are being rate limited! Retry after: %d Seconds!**", seconds));
                return;
            }
        }
        MusicPlayer player = MinnAudioManager.getPlayer(event.guild);
        if (event.allArguments.isEmpty()) {
            if(!player.isPlaying() && !player.getAudioQueue().isEmpty()) {
                player.play();
                event.sendMessage("Started playback!");
                return;
            }
            event.sendMessage("Queue is empty. You have to provide at least one URL.");
            return;
        }
        event.sendMessage("Validating request, this may take a few minutes..."
                        + (!event.guild.getAudioManager().isConnected()
                        ? String.format("\nIn the meantime you can make me connect to the channel you are in by typing `%sjoinme` while you are in a channel.", prefix)
                        : ""),
                msg -> executor.submit(() -> {
                    Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
                        t.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) logger);
                        event.sendMessage("**An error occurred, please direct this to the developer:** `L:" + e.getStackTrace()[0].getLineNumber()
                                + " - [" + e.getClass().getSimpleName() + "] " + e.getMessage() + "`");
                    });
                    String[] urls = event.allArguments.trim().replace(" ", "").split("\\Q,\\E");

                    final boolean[] error = {false};
                    for (String url : urls) {
                        if (url.contains("https://gaming.youtube.com/watch?v=")) {
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
                        // execute

                        for (AudioSource source : listSources) { // Use for/each to stop mutli process spawns
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
                            } else if (info.isLive()) {
                                event.sendMessage("Detected Live Stream. I don't play live streams. Skipping...");
                                return;
                            }
                            player.getAudioQueue().add(source);
                            if (!player.isPlaying()) {
                                msg.updateMessageAsync("Enqueuing songs and starting playback...", null);
                                try {
                                    Thread.sleep(3000); // Build buffer
                                } catch (InterruptedException ignored) {
                                }
                                player.play();
                                msg.updateMessageAsync("Now playing...", null);
                            }
                        }
                    }
                }));
        limitation.put(event.guild.getId(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5L));
    }

    public String usage() {
        return String.format("Supporting Youtube, Soundcloud and direct URLs! `%splay 9M4BVITnto0`", prefix);
    }

    @Override
    public String getAlias() {
        return "play <URL>";
    }

    @Override
    public String example() {
        return "play 9M4BVITnto0";
    }

}
