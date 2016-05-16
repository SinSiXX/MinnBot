package minn.minnbot.entities.command.custom;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

/**
 * This command is only meant to be used by Minn, not guaranteed to work and no support to be expected.
 */
public class InviteCommand extends CommandAdapter {

    private Guild home;
    private TextChannel channel;

    public InviteCommand(String prefix, Logger logger, Guild home) {
        init(prefix, logger);
        this.home = home;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getGuild() != home)
            return;
        super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        // https://canary.discordapp.com/channels/140412328733704192/141567013813354496
        if (!event.allArguments.contains("https://discordapp.com/oauth2/authorize?&client_id="))
            return;
        if (channel == null) {
            channel = event.jda.getTextChannelById("141567013813354496");
        }
        channel.sendMessageAsync(event.author.getAsMention() + " requested: " + event.allArguments,
                (m) -> event.sendMessage("Your request has been queued!"));
    }

    @Override
    public String getAlias() {
        return "invite";
    }
}
