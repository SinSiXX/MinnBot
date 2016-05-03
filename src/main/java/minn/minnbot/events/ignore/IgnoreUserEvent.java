package minn.minnbot.events.ignore;

import net.dv8tion.jda.entities.User;

public class IgnoreUserEvent extends IgnoreEvent {

    public final User user;

    public IgnoreUserEvent(User user) {
        this.user = user;
    }

}
