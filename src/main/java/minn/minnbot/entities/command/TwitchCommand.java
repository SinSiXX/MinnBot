package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.TwitchUtil;

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
                if (channel.getGame().isEmpty())
                    m.updateMessageAsync(String.format("**%s** is streaming for **%d viewers" +
                            "\n__Title:__%s" +
                            "\n__Preview:__** %s" +
                            "\n**__Link:__** <%s>", channel.getName().toUpperCase(), stream.getViewers(), channel.getStatus(), stream.getPreview(TwitchUtil.Stream.PreviewType.LARGE), stream.getURL()).replace("@", "\u0001@\u0001"), null);
                else
                    m.updateMessageAsync(String.format("**%s** is playing **%s** for **%d viewers" +
                            "\n__Title:__ %s" +
                            "\n__Preview:__** %s" +
                            "\n**__Link:__** <%s>", channel.getName().toUpperCase(), channel.getGame(), stream.getViewers(), channel.getStatus(), stream.getPreview(TwitchUtil.Stream.PreviewType.LARGE), stream.getURL()).replace("@", "\u0001@\u0001"), null);
            } catch (Exception e) {
                m.updateMessageAsync(String.format("**%s\nStreamer \"%s\" is not live or doesn't exist!**", e.getMessage(), event.allArguments.replace(" ", "_").replace("@", "\u0001@\u0001")), null);
            }
        });
    }

    @Override
    public String getAlias() {
        return "twitch <name>";
    }

    public String example() {
        return "twitch popskyy";
    }
}
