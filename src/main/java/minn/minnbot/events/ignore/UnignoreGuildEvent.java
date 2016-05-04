package minn.minnbot.events.ignore;

import net.dv8tion.jda.entities.Guild;

public class UnignoreGuildEvent extends IgnoreGuildEvent {
    public UnignoreGuildEvent(Guild guild) {
        super(guild);
    }
}
