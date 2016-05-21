package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import minn.minnbot.manager.QueueRequestManager;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.player.MusicPlayer;
import net.dv8tion.jda.player.Playlist;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;

import java.util.List;

public class QueueCommand extends CommandAdapter {

    public QueueCommand(String prefix, Logger logger) {
        super.init(prefix, logger);
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isPrivate())
        super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.allArguments.isEmpty()) {
            event.sendMessage("You have to provide at least one URL.");
            return;
        }

        if(MinnAudioManager.isLive(MinnAudioManager.getPlayer(event.guild))) {
            event.sendMessage("You cannot queue songs while the player is connected to a stream!");
            return;
        }

        event.sendMessage("Validating request, this may take a few minutes..."
                        + (!event.guild.getAudioManager().isConnected()
                        ? String.format("\nIn the meantime you can make me connect to the channel you are in by typing `%sjoinme`.", prefix)
                        : ""),
                msg -> QueueRequestManager.requestEnqueue(event.guild, (accepted) -> {
                    final boolean[] sent = {msg != null};
                    if(!accepted) {
                        if(sent[0]) msg.updateMessageAsync("**All request slots are filled. Try again in a few minutes!**", m -> sent[0] = m != null);
                        return;
                    }
                    Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
                        t.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) logger);
                        event.sendMessage("**An error occurred, please direct this to the developer:** `L:" + e.getStackTrace()[0].getLineNumber()
                                + " - [" + e.getClass().getSimpleName() + "] " + e.getMessage() + "`");
                    });
                    String[] urls = event.allArguments.trim().replaceAll("\\s+", "").split(",");
                    MusicPlayer player = MinnAudioManager.getPlayer(event.guild);
                    final boolean[] error = {false};
                    for (String url : urls) {
                        if (url.contains("https://gaming.youtube.com/watch?v=")) {
                            if(sent[0]) msg.updateMessageAsync("Youtube Gaming URLs are not accepted. Skipping...", m -> sent[0] = m != null);
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
                            if(sent[0]) msg.updateMessageAsync(String.format("Detected Playlist with **%d** entries! Starting to queue songs...", listSources.size()), m -> sent[0] = m != null);
                        } else if (listSources.size() == 1) {
                            AudioInfo audioInfo = listSources.get(0).getInfo();
                            if (audioInfo.getError() != null) {
                                if(sent[0]) msg.updateMessageAsync("**__Error with source:__ " + audioInfo.getError().trim() + "**", m -> sent[0] = m != null);
                                continue;
                            }
                            if(sent[0]) msg.updateMessageAsync("Adding `" + audioInfo.getTitle().replace("`", "\u0001`\u0001") + "` to the queue!", m -> sent[0] = m != null);
                        } else {
                            if(sent[0]) msg.updateMessageAsync("Source had no attached/readable information. Skipping...", m -> sent[0] = m != null);
                            continue;
                        }
                        // execute

                        for (AudioSource source : listSources) { // Use for/each to stop mutli process spawns
                            AudioInfo info = source.getInfo();
                            if (info == null) {
                                if (!error[0]) {
                                    if(sent[0]) msg.updateMessageAsync("Source was not available. Skipping.", m -> sent[0] = m != null);
                                    error[0] = true;
                                }
                                continue;
                            } else if (info.getError() != null) {
                                if (!error[0]) {
                                    if(sent[0]) msg.updateMessageAsync("**One or more sources were not available. Sorry fam.**", m -> sent[0] = m != null);
                                    error[0] = true;
                                }
                                continue;
                            } else if (info.isLive()) {
                                if(sent[0]) msg.updateMessageAsync("Detected Live Stream. I don't play live streams. Skipping...", m -> sent[0] = m != null);
                                continue;
                            }
                            player.getAudioQueue().add(source);
                            if (!player.isPlaying()) {
                                if(sent[0]) msg.updateMessageAsync("Enqueuing songs and starting playback...", m -> sent[0] = m != null);
                                try {
                                    Thread.sleep(3000); // Build buffer
                                } catch (InterruptedException ignored) {
                                }
                                player.play();
                                if(sent[0]) msg.updateMessageAsync("**Now playing...**", m -> sent[0] = m != null);
                            }
                        }
                    }
                    QueueRequestManager.dequeue(event.guild);
                }));
    }

    @Override
    public String getAlias() {
        return "queue <URL>, <URL>, <URL>, ...";
    }

    @Override
    public String example() {
        return "queue <https://www.youtube.com/watch?v=58mah_0Y8TU> , 58mah_0Y8TU,58mah_0Y8TU";
    }
}

