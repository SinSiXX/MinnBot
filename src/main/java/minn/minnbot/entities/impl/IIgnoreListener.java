package minn.minnbot.entities.impl;

import minn.minnbot.entities.IgnoreListener;
import minn.minnbot.entities.PublicLog;
import minn.minnbot.events.ignore.*;

public class IIgnoreListener extends IgnoreListener {

    @Override
    protected void onIgnoredUser(IgnoreUserEvent event) {
        PublicLog.log("**__Ignored:__ " + event.user.getUsername().replace("**", "*\u0001*\u0001") + "#" + event.user.getDiscriminator() + "**");
    }

    @Override
    protected void onUnignoredUser(UnignoreUserEvent event) {
        PublicLog.log("**__Un-ignored:__ " + event.user.getUsername().replace("**", "*\u0001*\u0001") + "#" + event.user.getDiscriminator() + "**");
    }

    @Override
    protected void onIgnoredGuild(IgnoreGuildEvent event) {
        PublicLog.log("**__Ignored:__ " + event.guild.getName().replace("**", "*\u0001*\u0001") + "**");
    }

    @Override
    protected void onUnignoredGuild(UnignoreGuildEvent event) {
        PublicLog.log("**__Un-ignored:__ " + event.guild.getName().replace("**", "*\u0001*\u0001") + "**");
    }

    @Override
    protected void onIgnoredChannel(IgnoreChannelEvent event) {
        PublicLog.log("**__Ignored:__ #" + (event.channel.getName() + " - in - " + event.channel.getGuild().getName()).replace("**", "*\u0001*\u0001") + "**");
    }

    @Override
    protected void onUnignoredChannel(UnignoreChannelEvent event) {
        PublicLog.log("**__Un-ignored:__ #" + (event.channel.getName() + " - in - " + event.channel.getGuild().getName()).replace("**", "*\u0001*\u0001") + "**");
    }

    public IIgnoreListener() {
    }

}
