package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.RemoteSource;

public class AudioInfoCommand extends CommandAdapter {

    public AudioInfoCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        if(event.allArguments.isEmpty()) {
            event.sendMessage("Unable to get info!");
            return;
        }
        AudioSource source = new RemoteSource((event.allArguments.matches("<[a-zA-Z/.:]+>") ? event.allArguments.substring(1, event.allArguments.length()-1) : event.allArguments));

        AudioInfo info = source.getInfo();
        if (info.getError() != null) {
            event.sendMessage(String.format("**__Error:__ %s**", info.getError()));
            return;
        }
        String title = info.getTitle();
        String duration = info.getDuration().getFullTimestamp();
        String extractor = info.getExtractor();
        String thumbnail = info.getThumbnail();
        boolean isLive = info.isLive();

        event.sendMessage(String.format("```\nTitle: %s\nDuration: %s\nLive: %b\nExtractor: %s\nThumbnail: %s```", title, duration, isLive, extractor, thumbnail));

    }

    @Override
    public String getAlias() {
        return "getInfo <url>";
    }

    public String example() {
        return "getInfo Knjgj56khk";
    }

}
