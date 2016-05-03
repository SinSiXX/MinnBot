package minn.minnbot.events.ignore;

import net.dv8tion.jda.entities.TextChannel;

public class IgnoreChannelEvent extends IgnoreEvent {

    public final TextChannel channel;

    public IgnoreChannelEvent(TextChannel channel) {
        this.channel = channel;
    }

}
