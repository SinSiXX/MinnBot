package minn.minnbot.entities.impl;

import minn.minnbot.entities.IgnoreListener;
import minn.minnbot.events.ignore.*;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.TextChannel;

public class IIgnoreListener extends IgnoreListener {

    private TextChannel channel;

    @Override
    protected void onIgnoredUser(IgnoreUserEvent event) {
        channel.sendMessageAsync("**__Ignored:__ " + event.user.getUsername().replace("**", "*\u0001*\u0001") + "#" + event.user.getDiscriminator() + "**", null);
    }

    @Override
    protected void onUnignoredUser(UnignoreUserEvent event) {
        channel.sendMessageAsync("**__Unignored:__ " + event.user.getUsername().replace("**", "*\u0001*\u0001") + "#" + event.user.getDiscriminator() + "**", null);
    }

    @Override
    protected void onIgnoredGuild(IgnoreGuildEvent event) {
        channel.sendMessageAsync("**__Ignored:__ " + event.guild.getName().replace("**", "*\u0001*\u0001") + "**", null);
    }

    @Override
    protected void onUnignoredGuild(UnignoreGuildEvent event) {
        channel.sendMessageAsync("**__Unignored:__ " + event.guild.getName().replace("**", "*\u0001*\u0001") + "**", null);
    }

    @Override
    protected void onIgnoredChannel(IgnoreChannelEvent event) {
        channel.sendMessageAsync("**__Ignored:__ #" + (event.channel.getName() + " - in - " + event.channel.getGuild()).replace("**", "*\u0001*\u0001") + "**", null);
    }

    @Override
    protected void onUnignoredChannel(UnignoreChannelEvent event) {
        channel.sendMessageAsync("**__Unignored:__ #" + (event.channel.getName() + " - in - " + event.channel.getGuild()).replace("**", "*\u0001*\u0001") + "**", null);
    }

    public IIgnoreListener(TextChannel logChannel) {
        if(logChannel == null)
            throw new NullPointerException();
        channel = logChannel;
        if (!channel.checkPermission(channel.getJDA().getSelfInfo(), Permission.MESSAGE_WRITE))
            throw new IllegalArgumentException("Bot is unable to send messages in the given channel.");
    }

}
