package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.audio.MinnPlayer;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.EmoteUtil;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.entities.VoiceStatus;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class JoinCommand extends CommandAdapter {

    private MinnPlayer player;

    public JoinCommand(String prefix, Logger logger, MinnPlayer player) {
        this.prefix = prefix;
        this.logger = logger;
        this.player = player;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.isPrivate())
            return;
        super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        User user = event.event.getAuthor();
        Guild guild = event.event.getGuild();
        VoiceStatus status = guild.getVoiceStatusOfUser(user);
        if (status == null) {
            event.sendMessage("You must be in a voice channel to use this command.");
            return;
        }
        if (status.getChannel() == null) {
            event.sendMessage("You must be in a voice channel to use this command.");
            return;
        }
        VoiceChannel channel = status.getChannel();
        VoiceStatus myStatus = guild.getVoiceStatusOfUser(event.event.getJDA().getSelfInfo());
        try {
            if (myStatus == null || myStatus.getChannel() == null) {
                guild.getAudioManager().openAudioConnection(channel);
            } else {
                guild.getAudioManager().moveAudioConnection(channel);
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
          //  event.sendMessage("I am unable to join that voice channel.");
            return;
        }
        guild.getAudioManager().setSendingHandler(player);
        event.sendMessage("Joined `" + channel.getName() + "`! " + EmoteUtil.getRngOkHand());
    }

    @Override
    public boolean isCommand(String message) {
        try {
            String cmd = message.split(" ", 2)[0];
            return cmd.equalsIgnoreCase(prefix + "joinme");
        } catch (IndexOutOfBoundsException ignore) {
        }
        return false;
    }

    @Override
    public String getAlias() {
        return "joinme";
    }
}