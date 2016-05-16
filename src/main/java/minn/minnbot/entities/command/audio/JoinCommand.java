package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import minn.minnbot.util.EmoteUtil;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class JoinCommand extends CommandAdapter {

    public JoinCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isPrivate())
            super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.allArguments.isEmpty()) {
            event.sendMessage("Try joinme to make the bot join **your** channel or specify the channel via name, if multiple channels have the same name I will pick the first one.");
            return;
        }
        VoiceChannel channel = getChannelFromName(event.allArguments, event.guild);
        if (channel == null) {
            event.sendMessage("Channel was not found. " + EmoteUtil.getRngThumbsdown() + usage());
            return;
        }
        if (!channel.checkPermission(event.jda.getSelfInfo(), Permission.VOICE_CONNECT)) {
            event.sendMessage("I am unable to connect to **" + channel.getName() + "**. " + EmoteUtil.getRngThumbsdown());
            return;
        }
        if (!channel.checkPermission(event.jda.getSelfInfo(), Permission.VOICE_SPEAK)) {
            event.sendMessage("I am unable to speak in **" + channel.getName() + "**. " + EmoteUtil.getRngThumbsdown());
            return;
        }
        if (event.guild.getAudioManager().isAttemptingToConnect()) {
            event.sendMessage("I am already trying to connect to another channel. Try changing the voice region to fix me.");
            return;
        }
        if (!event.guild.getAudioManager().isConnected())
            event.guild.getAudioManager().openAudioConnection(channel);
        else
            event.guild.getAudioManager().moveAudioConnection(channel);
        MinnAudioManager.getPlayer(event.guild); // to activate the keepAlive
        event.sendMessage("Connected to **" + channel.getName() + "** " + EmoteUtil.getRngOkHand());
    }

    private VoiceChannel getChannelFromName(String name, Guild guild) {
        return guild.getVoiceChannels().parallelStream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public String getAlias() {
        return "join <channel>";
    }

    public String example() {
        return "join Music";
    }

    public String usage() {
        return "\n**" + prefix + "join <channel>**\n" +
                "**channel - the name of the voice channel the bot is supposed to join.**";
    }

}
