package minn.minnbot.entities.command.statistics;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.MessageHistory;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.User;

import java.util.LinkedList;
import java.util.List;

public class MessagesCommand extends CommandAdapter {

    public MessagesCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        List<User> mentions = event.message.getMentionedUsers();

        int amount = 5;
        try {
            if (mentions.size() > 0)
                amount = Integer.parseInt(event.arguments[1]);
            else
                amount = Integer.parseInt(event.allArguments);
        } catch (IndexOutOfBoundsException | NumberFormatException ignored) {
        }
        amount = Math.max(Math.min(amount, 10), 1); // number between 1-10

        User user = event.author;
        if (mentions.size() > 0) {
            user = mentions.get(0);
        }

        List<Message> hist = new MessageHistory(event.channel).retrieve(100);
        List<Message> messages = new LinkedList<>();

        final int[] count = {0};
        User finalUser = user;
        int finalAmount = amount;

        hist.remove(event.message);
        hist.stream().forEachOrdered(message -> {
            if (message.getAuthor() != null && message.getAuthor() == finalUser && count[0] < finalAmount) {
                messages.add(message);
                count[0]++;
            }
        });

        final String[] s = {"**Messages:**```"};
        messages.stream().forEachOrdered(message -> {
            if ((s[0] + "\n\n" + message.getContent() + "```").length() >= 1500) {
                return;
            }
            s[0] += "\n\n" + message.getContent().replaceAll("(```)", "\u0001`\u0001`\u0001`\u0001").replaceAll("(<@)", "<@\u0001");
        });
        event.sendMessage(s[0] + "```");
    }

    @Override
    public String getAlias() {
        return "messages <@mention> <amount>";
    }

    public String example() {
        return "messages <@158174948488118272> 5";
    }

    public String usage() {
        return "Returns the last *amount* messages of the mentioned user.";
    }
}
