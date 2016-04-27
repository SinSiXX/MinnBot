package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.player.MusicPlayer;

public class SkipCommand extends CommandAdapter {

    public SkipCommand(String prefix, Logger logger) {
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
        if(!player.isPlaying()) {
            event.sendMessage("Player is not playing!");
            return;
        }
        player.skipToNext();
        event.sendMessage("Skipped song!");
    }

    @Override
    public boolean isCommand(String message) {
        String[] p = message.split(" ",2);
        return p.length > 0 && p[0].equalsIgnoreCase(prefix + "skip");
    }

    @Override
    public String getAlias() {
        return "skip";
    }

    @Override
    public String example() {
        return "skip";
    }
}
