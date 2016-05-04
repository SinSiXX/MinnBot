package minn.minnbot.events.ignore;

import net.dv8tion.jda.entities.TextChannel;

public class UnignoreChannelEvent extends IgnoreChannelEvent {
    public UnignoreChannelEvent(TextChannel channel) {
        super(channel);
    }
}
