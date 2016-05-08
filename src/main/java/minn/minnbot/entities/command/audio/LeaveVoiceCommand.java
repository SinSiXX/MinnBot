package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.EmoteUtil;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.VoiceStatus;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.List;

public class LeaveVoiceCommand extends CommandAdapter {

    public LeaveVoiceCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isPrivate())
            super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        Guild guild = event.event.getGuild();
        VoiceStatus status = guild.getVoiceStatusOfUser(event.event.getJDA().getSelfInfo());
        if (status == null || status.getChannel() == null) {
            event.sendMessage("I'm not even in a voice channel. pls :pensive:");
            return;
        }
        guild.getAudioManager().closeAudioConnection();
        event.sendMessage(EmoteUtil.getRngOkHand());
    }

    @Override
    public boolean isCommand(String message,List<String> prefixList) {
        String[] p = message.split(" ", 2);
        if(p.length < 1)
            return false;
        if(p[0].equalsIgnoreCase(prefix + "vLeave"))
            return true;
        for(String fix : prefixList) {
            if(p[0].equalsIgnoreCase(fix + "vLeave"))
                return true;
        }
        return false;
    }

    @Override
    public String getAlias() {
        return "vLeave";
    }

}
