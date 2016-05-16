package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import minn.minnbot.util.EmoteUtil;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

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
        if (!event.guild.getAudioManager().isConnected()) {
            event.sendMessage("I'm not even in a voice channel. pls :pensive:");
            return;
        }
        event.guild.getAudioManager().closeAudioConnection();
        MinnAudioManager.reset(event.guild);
        event.sendMessage(EmoteUtil.getRngOkHand());
    }

    @Override
    public String getAlias() {
        return "vLeave";
    }

}
