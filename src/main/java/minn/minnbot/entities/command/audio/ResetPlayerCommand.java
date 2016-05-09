package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.player.MusicPlayer;

public class ResetPlayerCommand extends CommandAdapter {

    public ResetPlayerCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.isPrivate())
            return;
        super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        MinnAudioManager.reset(event.guild);
        event.sendMessage("Resetting player...");
        MinnAudioManager.registerPlayer(new MusicPlayer(), event.guild);
        event.guild.getAudioManager().setSendingHandler(MinnAudioManager.getPlayer(event.guild));
    }

    @Override
    public String getAlias() {
        return "vReset";
    }
}
