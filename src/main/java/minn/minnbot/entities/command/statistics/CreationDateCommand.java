package minn.minnbot.entities.command.statistics;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.TimeUtil;
import net.dv8tion.jda.entities.User;

import java.util.LinkedList;
import java.util.List;

public class CreationDateCommand extends CommandAdapter {

    public CreationDateCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        List<User> mentions = event.message.getMentionedUsers();
        if(mentions.isEmpty()) {
            String nick = event.guild.getNicknameForUser(event.author);
            if(nick == null)
                nick = event.author.getUsername();
            event.sendMessage(String.format("**%s** created their account at **%s**", nick, TimeUtil.getCreationTime(Long.parseLong(event.author.getId()))));
            return;
        }
        String s = "**Join dates for user" + (mentions.size() == 1 ? "" : "s:") + "**";
        List<User> passed = new LinkedList<>();
        for(int i = 0; i < mentions.size() && i < 5; i++) {
            if(passed.contains(mentions.get(i)))
                continue;
            String nick = event.guild.getNicknameForUser(mentions.get(i));
            if(nick == null)
                nick = mentions.get(i).getUsername();
            s += String.format("\n**%s** created their account at **%s**", nick.replaceAll("@", "@\u0001"), TimeUtil.getCreationTime(Long.parseLong(mentions.get(i).getId())));
            passed.add(mentions.get(i));
        }
        event.sendMessage(s);
    }

    @Override
    public String getAlias() {
        return "creationDate [mention]";
    }

    public String example() {
        return "creationDate";
    }
}
