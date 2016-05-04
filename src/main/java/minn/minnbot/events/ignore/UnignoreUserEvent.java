package minn.minnbot.events.ignore;

import net.dv8tion.jda.entities.User;

public class UnignoreUserEvent extends IgnoreUserEvent {

    public UnignoreUserEvent(User user) {
        super(user);
    }

}
