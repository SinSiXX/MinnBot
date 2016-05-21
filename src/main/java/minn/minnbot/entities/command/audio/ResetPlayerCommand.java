package minn.minnbot.entities.command.audio;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import minn.minnbot.manager.QueueRequestManager;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

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
        event.sendMessage("Resetting player...");
        QueueRequestManager.dequeue(event.guild);
        MinnAudioManager.reset(event.guild);
    }

    @Override
    public String getAlias() {
        return "vReset";
    }
}
