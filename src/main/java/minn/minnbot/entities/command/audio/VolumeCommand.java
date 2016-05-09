package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.player.MusicPlayer;

public class VolumeCommand extends CommandAdapter {

    public VolumeCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.isPrivate())
            return;
        super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        MusicPlayer player = MinnAudioManager.getPlayer(event.event.getGuild());
        if (event.allArguments.isEmpty()) {
            event.sendMessage("**__Volume:__** " + player.getVolume());
        } else {
            try {
                float vol = Float.parseFloat(event.allArguments);
                if(vol > 1) {
                    vol = 1;
                } else if(vol < 0) {
                    vol = 0;
                }
                player.setVolume(vol);
                event.sendMessage("**__Volume:__** " + vol);
            } catch (NumberFormatException ignored) {
                event.sendMessage("Volume must be a number between 1 and 0!");
            }

        }
    }

    @Override
    public String getAlias() {
        return "volume";
    }

    @Override
    public String example() {
        return "volume .5";
    }
}
