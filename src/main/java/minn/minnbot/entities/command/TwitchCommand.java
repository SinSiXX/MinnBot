package minn.minnbot.entities.command;

import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.TimeUtil;
import minn.minnbot.util.TwitchUtil;

import java.rmi.UnexpectedException;

public class TwitchCommand extends CommandAdapter {

    public TwitchCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.allArguments.isEmpty()) {
            event.sendMessage("I was unable to find a stream with no name sorry. :pensive:");
            return;
        }
        event.sendMessage("**Fetching information...**", m -> {
            try {
                TwitchUtil.Stream stream = TwitchUtil.getStream(event.allArguments.replace(" ", "_"));
                TwitchUtil.Channel channel = stream.getChannel();
                String tags = channel.isMature() ? channel.isPartnered() ? "[Mature/Partner]" : "[Mature]" : channel.isPartnered() ? "[Partner]" : "";
                if (channel.getGame().isEmpty())
                    m.updateMessageAsync(String.format(tags.isEmpty() ? "" : "**%s**\n", tags) + String.format("**%s** is streaming for **%s** with uptime: **%s**" +
                            "\n**__Title:__ %s" +
                            "\n__Preview:__** %s" +
                            "\n**__Link:__** <%s>", channel.getName().toUpperCase(), (stream.getViewers() == 1 ? "1 viewer" : stream.getViewers() + " viewers"), TimeUtil.uptime(stream.getUptime()), protect(channel.getStatus()), stream.getPreview(TwitchUtil.Stream.PreviewType.LARGE), stream.getURL()), null);
                else
                    m.updateMessageAsync(String.format(tags.isEmpty() ? "" : "**%s**\n", tags) + String.format("**%s** is playing **%s** for **%s** with uptime: **%s**" +
                            "\n**__Title:__ %s" +
                            "\n__Preview:__** %s" +
                            "\n**__Link:__** <%s>", channel.getName().toUpperCase(), channel.getGame(), (stream.getViewers() == 1 ? "1 viewer" : stream.getViewers() + " viewers"), TimeUtil.uptime(stream.getUptime()), protect(channel.getStatus()), stream.getPreview(TwitchUtil.Stream.PreviewType.LARGE), stream.getURL()), null);
            } catch (Exception e) {
                try {
                    TwitchUtil.Channel channel = TwitchUtil.getChannel(event.allArguments.replace(" ", "_"));
                    String tags = channel.isMature() ? channel.isPartnered() ? "[Mature/Partner]" : "[Mature]" : channel.isPartnered() ? "[Partner]" : "";
                    m.updateMessageAsync(String.format(tags.isEmpty() ? "" : "**%s**\n", tags) + String.format("**%s** is not live%s", channel.getName().toUpperCase(), channel.getGame().isEmpty() ? "." : " and was last seen playing **" + channel.getGame() + "**."), null);
                } catch (UnexpectedException | UnirestException e1) {
                    m.updateMessageAsync(String.format("**%s!**", e.getMessage()), null);
                }
            }
        });
    }

    private String protect(String input) {
        return input.replace("@", "\u0001@\u0001").replaceAll("(http://|https://)", "");
    }

    @Override
    public String getAlias() {
        return "twitch <name>";
    }

    public String example() {
        return "twitch popskyy";
    }
}
