package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.player.MusicPlayer;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.AudioTimestamp;

import java.util.List;

public class CurrentCommand extends CommandAdapter {

    public CurrentCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isPrivate())
            super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        MusicPlayer player = MinnAudioManager.getPlayer(event.event.getGuild());

        if(MinnAudioManager.isLive(player)) {
            try {
                event.sendMessage(String.format("**Playing live stream: %s**", player.getCurrentAudioSource().getInfo().getTitle()));
            } catch (Exception e) {
                event.sendMessage("**Playing live stream: N/A**");
            }
            return;
        }

        List<AudioSource> playlist = player.getAudioQueue();
        AudioSource previous = player.getPreviousAudioSource();
        AudioSource current = player.getCurrentAudioSource();
        int totalSeconds = 0;
        String s = "";
        if (previous != null) {
            AudioInfo info = previous.getInfo();
            if (info != null) {
                try {
                    s += "**__Previously:__** `" + (info.getTitle()).replace("`", "\u0001`").replace("[", "(").replace("]", ")") + "`\n";
                } catch (NullPointerException ignored) {
                    s += "**__Previously:__** `NaN`\n";
                }
            }
        }
        if (!player.isStopped() && current != null) {
            AudioInfo info = current.getInfo();
            totalSeconds += current.getInfo().getDuration().getTotalSeconds() - player.getCurrentTimestamp().getTotalSeconds();
            if (info != null) {
                try {
                    s += "**__Currently:__** `[" + player.getCurrentTimestamp().getTimestamp() + " / " + info.getDuration().getTimestamp() + "] " + (info.getTitle()).replace("`", "\u0001`").replace("[", "(").replace("]", ")") + "`\n";
                } catch (NullPointerException ignored) {
                    s += "**__Currently:__** `NaN`\n";
                } catch (Exception ignored) {
                    s += "**__Currently:__** `[NaN] " + (info.getTitle()).replace("`", "\u0001`".replace("[", "(").replace("]", ")")) + "`\n";
                }
            }
        }
        if (playlist.isEmpty()) {
            s += "**__No queued songs.__**\n";
        } else {
            int index = 1;
            boolean passedMax = false;
            s += "**__Queue:__ " + playlist.size() + " songs** ```md\n";
            for (AudioSource f : playlist) {
                totalSeconds += f.getInfo().getDuration().getTotalSeconds();
                if (index > 5) {
                    if (!passedMax) {
                        s += "...";
                        passedMax = true;
                    }
                    continue;
                }
                AudioInfo info = f.getInfo();
                if (info != null) {
                    try {
                        s += "[" + info.getDuration().getTimestamp() + "][" + (info.getTitle()).replace("`", "\u0001`").replace("[", "(").replace("]", ")") + "]\n";
                    } catch (NullPointerException ignored) {
                        continue;
                    } catch (Exception ignored) {
                        s += "[NaN][" + (info.getTitle()).replace("`", "\u0001`").replace("[", "(").replace("]", ")") + "]\n";
                    }
                    index++;
                }
            }
            s += "``` ";
        }
        String totalTimestamp = AudioTimestamp.fromSeconds(totalSeconds).getTimestamp();
        s += "**Total length: " + totalTimestamp + "**\n";
        event.sendMessage(s);
    }

    @Override
    public String getAlias() {
        return "current";
    }

    @Override
    public String example() {
        return "current";
    }
}
