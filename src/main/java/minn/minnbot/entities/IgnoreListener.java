package minn.minnbot.entities;

import minn.minnbot.events.ignore.*;

public abstract class IgnoreListener {

    protected void onIgnoredUser(IgnoreUserEvent event) {
    }

    protected void onUnignoredUser(UnignoreUserEvent event) {
    }

    protected void onIgnoredGuild(IgnoreGuildEvent event) {
    }

    protected void onUnignoredGuild(UnignoreGuildEvent event) {
    }

    protected void onIgnoredChannel(IgnoreChannelEvent event) {
    }

    protected void onUnignoredChannel(UnignoreChannelEvent event) {
    }

    public void onEvent(IgnoreEvent event) {
        if (event instanceof IgnoreUserEvent) {
            if (event instanceof UnignoreUserEvent)
                onUnignoredUser((UnignoreUserEvent) event);
            else
                onIgnoredUser((IgnoreUserEvent) event);
        } else if (event instanceof IgnoreGuildEvent) {
            if (event instanceof UnignoreGuildEvent)
                onUnignoredGuild((UnignoreGuildEvent) event);
            else
                onIgnoredGuild((IgnoreGuildEvent) event);
        } else if (event instanceof IgnoreChannelEvent) {
            if (event instanceof UnignoreChannelEvent)
                onUnignoredChannel((UnignoreChannelEvent) event);
            else
                onIgnoredChannel((IgnoreChannelEvent) event);
        }
    }
}
