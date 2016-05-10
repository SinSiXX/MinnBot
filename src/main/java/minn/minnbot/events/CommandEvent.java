package minn.minnbot.events;

import minn.minnbot.util.TimeUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.function.Consumer;

public class CommandEvent {

    public final String allArguments;
    public final String[] arguments;
    public final MessageReceivedEvent event;
    public final boolean isPrivate;
    public final Guild guild;
    public final JDA jda;
    public final MessageChannel channel;
    public final User author;
    public final String timeStamp;
    //private static boolean checked;
    public final Message message;

    /*public static void checked() {
        checked = true;
    }*/

    public CommandEvent(MessageReceivedEvent event) {
        String[] p = event.getMessage().getRawContent().split("\\s+", 2);
        if (p.length >= 2) {
            arguments = p[1].split("\\s+");
            allArguments = p[1];
        } else {
            arguments = new String[0];
            allArguments = "";
        }
        this.event = event;
        isPrivate = event.isPrivate();
        timeStamp = TimeUtil.timeStamp();
        channel = event.getChannel();
        jda = event.getJDA();
        guild = event.getGuild();
        author = event.getAuthor();
        message = event.getMessage();
    }

    public void sendMessage(String content) {
        content = content.replace("@everyone", "@\u0001everyone").replace("@here", "@\u0001here");
        if (content.length() < 2000 && (guild == null || (event.getTextChannel().checkPermission(jda.getSelfInfo(), Permission.MESSAGE_WRITE) && guild.checkVerification())))
            event.getChannel().sendMessageAsync(content, null);
    }

    public void sendMessage(String content, Consumer<Message> callback) {
        content = content.replace("@everyone", "@\u0001everyone").replace("@here", "@\u0001here");
        if (content.length() < 2000 && (guild == null || (event.getTextChannel().checkPermission(jda.getSelfInfo(), Permission.MESSAGE_WRITE)&& guild.checkVerification()))) {
            event.getChannel().sendMessageAsync(content, callback);
        }
    }

    public Message sendMessageBlocking(String content) {
        content = content.replace("@everyone", "@\u0001everyone").replace("@here", "@\u0001here");
        if (content.length() < 2000 && (guild == null || (event.getTextChannel().checkPermission(jda.getSelfInfo(), Permission.MESSAGE_WRITE) && guild.checkVerification())))
            return event.getChannel().sendMessage(content);
        return null;
    }

}
