package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.player.MusicPlayer;

import java.util.List;

public class ReloadCommand extends CommandAdapter {

    public ReloadCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isPrivate())
            return;
        super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        MusicPlayer player = MinnAudioManager.getPlayer(event.guild);
        if (player.isPlaying()) {
            event.sendMessage("Reloading player...");
            event.guild.getAudioManager().setSendingHandler(player);
            if (!player.getAudioQueue().isEmpty() && !player.isPlaying())
                player.play();
            return;
        }
        event.sendMessage("Player is not currently playing!");
    }

    @Override
    public boolean isCommand(String message, List<String> prefixList) {
        String[] p = message.split(" ", 2);
        if(p.length < 1)
            return false;
        if(p[0].equalsIgnoreCase(prefix + "reload"))
            return true;
        for(String fix : prefixList) {
            if(p[0].equalsIgnoreCase(fix + "reload"))
                return true;
        }
        return false;
    }

    @Override
    public String getAlias() {
        return "reload";
    }
}
