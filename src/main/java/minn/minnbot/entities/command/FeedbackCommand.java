package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.EmoteUtil;
import minn.minnbot.util.TimeUtil;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

public class FeedbackCommand extends CommandAdapter {

    private Map<User, Long> cooldowns = new HashMap<>();

    public FeedbackCommand(String prefix, Logger logger) {
        this.logger = logger;
        this.prefix = prefix;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isPrivate()) {
            return;
        }
        super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        TextChannel feedback_channel = event.event.getJDA().getTextChannelById("163277437461856257");
        if (feedback_channel == null || !feedback_channel.checkPermission(event.event.getJDA().getSelfInfo(), Permission.MESSAGE_WRITE)) {
            logger.logThrowable(new UnsupportedOperationException("Feedback channel in Minn Development unreachable!"));
            return;
        }
        User user = event.event.getAuthor();
        String msg = ("**__" + TimeUtil.timeStamp() + "Feedback:__** ```md\n[" + event.event.getGuild().getName() + "][" + user.getUsername().replace("```", "") + "#" + user.getDiscriminator() + "]: " + event.allArguments.replace("```", "") + "```").replace("@everyone", "(mass mention prevented)");
        if (cooldowns.containsKey(user)) {
            long cd = cooldowns.get(user);
            if (cd > System.currentTimeMillis() + 60000) {
                event.sendMessage("Hey! Don't spam the feedback stream! *(You have been rate limited)*");
                cooldowns.replace(user, cd, System.currentTimeMillis());
                return;
            }
        }

        if (msg.length() >= 1500) {
            event.sendMessage("Your feedback message is too long. Sorry fam.");
            return;
        }
        feedback_channel.sendMessageAsync(msg, null);
        event.sendMessage("Thank you for the feedback. " + EmoteUtil.getRngOkHand());
        if (cooldowns.containsKey(user)) {
            long cd = cooldowns.get(user);
            cooldowns.replace(user, cd, System.currentTimeMillis());
        } else
            cooldowns.put(user, System.currentTimeMillis());
    }

    @Override
    public boolean isCommand(String message) {
        String[] p = message.split(" ", 2);
        return p.length > 0 && (p[0].equalsIgnoreCase(prefix + "feedback") || p[0].equalsIgnoreCase(prefix + "fb"));
    }

    public String usage() {
        return "`feedback <text>`, `fb <text>`\n**__Example:__** `" + prefix + "fb I love your bot, but add this cool command!`";
    }

    @Override
    public String getAlias() {
        return "feedback <text>";
    }

    @Override
    public String example() {
        return "feedback I hate your bot!";
    }

}
