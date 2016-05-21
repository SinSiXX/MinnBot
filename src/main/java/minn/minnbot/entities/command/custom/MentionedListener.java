package minn.minnbot.entities.command.custom;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.List;

public class MentionedListener extends CommandAdapter{

    public MentionedListener(Logger logger) {
        this.logger = logger;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfInfo())) {
            onCommand(new CommandEvent(event, getAlias().split("\\s+")[0]));
        }
    }

    @Override
    public void onCommand(CommandEvent event) {
        event.sendMessage(event.author.getAsMention() + ", MENTIONS?????????");
    }

    @Override
    public boolean isCommand(String message, List<String> prefixList) {
        return false;
    }

    @Override
    public String getAlias() {
        return "";
    }
}
