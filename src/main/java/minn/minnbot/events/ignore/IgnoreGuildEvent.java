package minn.minnbot.events.ignore;

import net.dv8tion.jda.entities.Guild;

public class IgnoreGuildEvent extends IgnoreEvent {

    public final Guild guild;

    public IgnoreGuildEvent(Guild guild) {
        this.guild = guild;
    }

}
