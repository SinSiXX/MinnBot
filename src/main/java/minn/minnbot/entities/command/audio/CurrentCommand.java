package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import net.dv8tion.jda.player.MusicPlayer;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;

import java.util.List;

public class CurrentCommand extends CommandAdapter {

    public CurrentCommand(String prefix, Logger logger) {
        this.logger = logger;
        this.prefix = prefix;
    }

    @Override
    public void onCommand(CommandEvent event) {
        MusicPlayer player = MinnAudioManager.getPlayer(event.event.getGuild());
        List<AudioSource> playlist = player.getAudioQueue();
        AudioSource previous = player.getPreviousAudioSource();
        AudioSource current = player.getCurrentAudioSource();
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
            s += "**__No queued songs.__**";
        } else {
            int index = 1;
            s += "**__Queue:__ " + playlist.size() + " songs** ```md\n";
            for (AudioSource f : playlist) {
                if (index > 5) {
                    s += "...";
                    break;
                }
                AudioInfo info = f.getInfo();
                if(info != null) {
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
            s += "```";
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

    @Override
    public String example() {
        return "current";
    }
}
