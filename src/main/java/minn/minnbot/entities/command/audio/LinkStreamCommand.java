package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.player.MusicPlayer;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.RemoteSource;

public class LinkStreamCommand extends CommandAdapter {

    public LinkStreamCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if(!event.isPrivate())
            super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        if(event.allArguments.isEmpty()) {
            event.sendMessage("I am unable to link that stream!");
            return;
        }

        if(MinnAudioManager.isLive(MinnAudioManager.getPlayer(event.guild))) {
            event.sendMessage("Player is already linked to a stream, you have to first unlink that stream!");
            return;
        }

        event.sendMessage("Trying to get stream!", m -> {
            boolean sent = m != null;
            RemoteSource source = new RemoteSource(event.arguments[0]);
            AudioInfo info = source.getInfo();
            if(info.getError() != null) {
                if(sent) m.updateMessageAsync("I was unable to link that stream!", null);
                return;
            }
            if(!info.isLive()) {
                if(sent) m.updateMessageAsync("That is not a live stream!", null);
                return;
            }
            MinnAudioManager.reset(event.guild);
            MusicPlayer player = MinnAudioManager.getPlayer(event.guild);
            player.getAudioQueue().add(source);
            MinnAudioManager.setIsLive(player, true);
            player.play();
            if(sent) m.updateMessageAsync("Successfully added stream and started playing!", null);
        });

    }

    @Override
    public String getAlias() {
        return "linkStream <url>";
    }

    public String usage() {
        return "Links a live stream to your player! (Only one at a time)";
    }

    public String example() {
        return "linkStream https://twitch.tv/monstercat";
    }
}
