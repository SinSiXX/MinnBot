package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.MessageHistory;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Message;

import java.util.List;
import java.util.stream.Collectors;

public class FindCommand extends CommandAdapter {

    public FindCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }


    @Override
    public void onCommand(CommandEvent event) {
        if (event.allArguments.isEmpty()) {
            event.sendMessage("No match!");
            return;
        } else if (event.allArguments.length() > 200) {
            event.sendMessage("Regex > 200 is not accepted!");
            return;
        }
        if (!event.isPrivate && !event.event.getTextChannel().checkPermission(event.jda.getSelfInfo(), Permission.MESSAGE_HISTORY)) {
            event.sendMessage("I am unable to view the message history!");
            return;
        }
        event.sendMessage(String.format("Searching the most recent messages for \"%s\"...", event.allArguments), m -> {
            if (m == null) return;
            List<Message> hist = new MessageHistory(event.channel).retrieve(102).subList(2, 102);
            hist = hist.parallelStream().filter(m1 -> m1.getRawContent().matches(String.format("(%s)", event.allArguments))).collect(Collectors.toList());
            if (hist.isEmpty()) {
                m.updateMessageAsync(String.format("No matches found for \"%s\"", event.allArguments), null);
                return;
            }
            StringBuilder b = new StringBuilder(String.format("Found %d match%s:", hist.size(), hist.size() == 1 ? "" : "es"));
            for (int i = 0; i < hist.size() && i < 5; i++) {
                if (hist.get(i).getAuthor() != null) {
                        b.append("\n- ").append(hist.get(i).getAuthor().getUsername()).append("#").append(hist.get(i).getAuthor().getDiscriminator()).append(" > ").append(hist.get(i).getContent());
                }
            }
            m.updateMessageAsync(b.toString().replace("@", "@\u0001"), msg -> {
                if (msg == null) m.updateMessageAsync("I was unable to post my list. Too long possibly!", null);
            });
        });
    }

    @Override
    public String getAlias() {
        return "find <regex>";
    }

    public String example() {
        return "find (.*<!?[0-9]+>.*)";
    }

    public String usage() {
        return "Searches the last 100 messages for the given regex and prints at max 5! (Exact matching)";
    }

}
