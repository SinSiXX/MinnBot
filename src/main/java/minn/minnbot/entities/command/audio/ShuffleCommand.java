package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import minn.minnbot.util.EmoteUtil;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.player.MusicPlayer;
import net.dv8tion.jda.player.source.AudioSource;

import java.util.Collections;
import java.util.List;

public class ShuffleCommand extends CommandAdapter{

    public ShuffleCommand(String prefix, Logger logger) {
        super.init(prefix, logger);
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.isPrivate())
            return;
        super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        MusicPlayer player = MinnAudioManager.getPlayer(event.guild);
        List<AudioSource> sourceList = player.getAudioQueue();
        if(sourceList.isEmpty()) {
            event.sendMessage("An empty queue can not be shuffled. " + EmoteUtil.getRngThumbsdown());
            return;
        }
        Collections.shuffle(sourceList);
        event.sendMessage("Queue has been randomized! " + EmoteUtil.getRngOkHand());
    }

    @Override
    public boolean isCommand(String message, List<String> prefixList) {
        String[] p = message.split(" ",2);
        if(p.length < 1)
            return false;
        if(p[0].equalsIgnoreCase(prefix + "shuffle"))
            return true;
        for(String fix : prefixList) {
            if(p[0].equalsIgnoreCase(fix + "shuffle"))
                return true;
        }
        return false;
    }

    @Override
    public String getAlias() {
        return "shuffle";
    }
}
