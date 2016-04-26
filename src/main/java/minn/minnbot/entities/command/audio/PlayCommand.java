package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import minn.minnbot.util.EmoteUtil;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.player.MusicPlayer;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.RemoteSource;

public class PlayCommand extends CommandAdapter {

    public PlayCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isPrivate())
            return;
        super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        MusicPlayer player = MinnAudioManager.getPlayer(event.event.getGuild());
        if (event.allArguments.isEmpty()) {
            if (player.isPlaying()) {
                event.sendMessage("Player is already playing, try to reload!");
            } else {
                if (player.getAudioQueue().isEmpty()) {
                    event.sendMessage("Queue is empty and can not start playing.");
                    return;
                }
                player.play();
                event.sendMessage("Started playback...");
            }
            return;
        }
        event.sendMessage("Fetching information.");
        AudioSource s = new RemoteSource(
                ((event.allArguments.startsWith("<") && event.allArguments.endsWith(">"))
                        ? event.allArguments.substring(1, event.allArguments.length() - 1)
                        : event.allArguments));
        String error = s.getInfo().getError();
        if (error.isEmpty()) {
            player.getAudioQueue().add(s);
        } else {
            event.sendMessage("**__Error:__** `" + error + "`");
            return;
        }
        if (!player.isPlaying()) {
            player.play();
            event.sendMessage("Added provided URL to queue and the player started playing! " + EmoteUtil.getRngOkHand());
            return;
        }
        event.sendMessage("Added provided URL to queue! " + EmoteUtil.getRngThumbsup());
    }

    @Override
    public boolean isCommand(String message) {
        String[] parts = message.split(" ", 2);
        return parts.length > 0 && parts[0].equalsIgnoreCase(prefix + "play");
    }

    public String usage() {
        return "Supporting Youtube, Soundcloud and direct URLs! `" + prefix + "play 9M4BVITnto0`";
    }

    @Override
    public String getAlias() {
        return "play <URL>\t<-!- Only for single videos, no playlist detection. -->";
    }

    @Override
    public String example() {
        return "play 9M4BVITnto0";
    }

}
