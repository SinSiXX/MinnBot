package minn.minnbot.entities.impl;

import minn.minnbot.entities.IgnoreListener;
import minn.minnbot.entities.PublicLog;
import minn.minnbot.events.ignore.*;

public class IIgnoreListener extends IgnoreListener {

    private String format(boolean added, String s) {
        return String.format("```diff\n%s > %s```", (added ? "-" : "+"), s.replace("```", "\u0001`\u0001`\u0001`\u0001"));
    }

    @Override
    protected void onIgnoredUser(IgnoreUserEvent event) {
        PublicLog.log(format(true, String.format("Ignored: %s#%s", event.user.getUsername(), event.user.getDiscriminator())));
    }

    @Override
    protected void onUnignoredUser(UnignoreUserEvent event) {
        PublicLog.log(format(false, String.format("Un-ignored: %s#%s", event.user.getUsername(), event.user.getDiscriminator())));
    }

    @Override
    protected void onIgnoredGuild(IgnoreGuildEvent event) {
        PublicLog.log(format(true, String.format("Ignored: %s", event.guild.getName())));
    }

    @Override
    protected void onUnignoredGuild(UnignoreGuildEvent event) {
        PublicLog.log(format(false, String.format("Un-ignored: %s", event.guild.getName())));
    }

    @Override
    protected void onIgnoredChannel(IgnoreChannelEvent event) {
        PublicLog.log(format(true, String.format("Ignored: #%s", String.format("%s - in - %s", event.channel.getName(), event.channel.getGuild().getName()))));
    }

    @Override
    protected void onUnignoredChannel(UnignoreChannelEvent event) {
        PublicLog.log(format(false, String.format("Un-ignored: #%s", event.channel.getName() + " - in - " + event.channel.getGuild().getName())));
    }

    public IIgnoreListener() {
    }

}
