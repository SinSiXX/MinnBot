package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import minn.minnbot.util.EmoteUtil;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.player.MusicPlayer;

public class UnlinkStreamCommand extends CommandAdapter {

    public UnlinkStreamCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if(!event.isPrivate())
            super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        MusicPlayer player = MinnAudioManager.getPlayer(event.guild);
        if(!MinnAudioManager.isLive(player)) {
            event.sendMessage("Player is not linked to any stream!");
            return;
        }
        player.skipToNext();
        MinnAudioManager.setIsLive(player, false);
        event.sendMessage("Un-linked stream! " + EmoteUtil.getRngOkHand());
    }

    @Override
    public String getAlias() {
        return "unlinkStream";
    }

    public String usage() {
        return "Un-links a live stream from your player!";
    }
}
