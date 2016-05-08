package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import minn.minnbot.util.EmoteUtil;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.entities.VoiceStatus;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.List;

public class JoinMeCommand extends CommandAdapter {


    public JoinMeCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isPrivate())
            super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        VoiceStatus status = event.guild.getVoiceStatusOfUser(event.author);
        if(event.guild.getAudioManager().isAttemptingToConnect()) {
            event.sendMessage("I am currently trying to connect to another channel.\nIf I'm stuck joining for more than a minute, consider changing the voice region.");
            return;
        }
        if (status == null) {
            event.sendMessage("You must be in a voice channel to use this command. If you are, please reconnect.");
            return;
        }
        if (status.getChannel() == null) {
            event.sendMessage("You must be in a voice channel to use this command.");
            return;
        }
        VoiceChannel channel = status.getChannel();
        if (!channel.checkPermission(event.jda.getSelfInfo(), Permission.VOICE_CONNECT)) {
            event.sendMessage("I am not allowed to join you :(");
            return;
        }
        if (!channel.checkPermission(event.jda.getSelfInfo(), Permission.VOICE_SPEAK)) {
            event.sendMessage("I wouldn't be able to speak there anyway :(");
            return;
        }
        event.guild.getAudioManager().setConnectTimeout(5000);
        if (!event.guild.getAudioManager().isConnected()) {
            event.guild.getAudioManager().openAudioConnection(channel);
        } else {
            event.guild.getAudioManager().moveAudioConnection(channel);
        }
        MinnAudioManager.getPlayer(event.guild); // to activate the keepAlive
        event.sendMessage("Joined `" + channel.getName() + "`! " + EmoteUtil.getRngOkHand());
    }

    @Override
    public boolean isCommand(String message, List<String> prefixList) {
            String[] p = message.split(" ", 2);
            if(p.length < 1)
                return false;
            if(p[0].equalsIgnoreCase(prefix + "joinme"))
                return true;
            for(String fix : prefixList) {
                if(p[0].equalsIgnoreCase(fix + "joinme"))
                    return true;
            }
            return false;
    }

    @Override
    public String getAlias() {
        return "joinme";
    }

    @Override
    public String example() {
        return "joinme";
    }

}
