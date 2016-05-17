package minn.minnbot.events;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageDeleteEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.events.message.MessageUpdateEvent;
import net.dv8tion.jda.exceptions.RateLimitedException;
import net.dv8tion.jda.hooks.ListenerAdapter;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
    //private static boolean checked;
    public final Message message;

    public final Node node;

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
        channel = event.getChannel();
        jda = event.getJDA();
        guild = event.getGuild();
        author = event.getAuthor();
        message = event.getMessage();
        node = new Node(jda, message);
    }

    public void sendMessage(String content) {
        if (content == null || content.isEmpty())
            throw new IllegalArgumentException("Content to send in CommandEvent was null or empty!");
        content = content.replace("@everyone", "@\u0001everyone").replace("@here", "@\u0001here");
        try {
            channel.sendMessageAsync(content, node::setResponse);
        } catch (Exception ignored) {
        }
    }

    public void sendMessage(String content, Consumer<Message> callback) {
        if (content == null || content.isEmpty())
            throw new IllegalArgumentException("Content to send in CommandEvent was null or empty!");
        content = content.replace("@everyone", "@\u0001everyone").replace("@here", "@\u0001here");
        try {
            channel.sendMessageAsync(content, m -> {
                node.setResponse(m);
                callback.accept(m);
            });
        } catch (Exception ignored) {
            callback.accept(null);
        }
    }

    public Message sendMessageBlocking(String content) {
        if (content == null || content.isEmpty())
            throw new IllegalArgumentException("Content to send in CommandEvent was null or empty!");
        content = content.replace("@everyone", "@\u0001everyone").replace("@here", "@\u0001here");
        try {
            Message m = channel.sendMessage(content);
            node.setResponse(m);
            return m;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static class Node extends ListenerAdapter {

        Message response;
        Message init;
        Timer keepAlive = new Timer("Checker", true);
        JDA jda;
        private static ThreadPoolExecutor executor = new ThreadPoolExecutor(30, 50, 5L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), r -> {
            Thread t = new Thread(r, "Checker-Pickup");
            t.setDaemon(true);
            t.setPriority(1);
            return t;
        });

        private Node(JDA jda, Message init) {
            this.jda = jda;
            this.init = init;
            Node n = this;
            keepAlive.schedule(new TimerTask() {
                @Override
                public void run() {
                    jda.removeEventListener(n);
                }
            }, TimeUnit.MINUTES.toMillis(5L));
            jda.addEventListener(this);
        }

        public void onMessageUpdate(MessageUpdateEvent event) {
            react(event.getMessage().getId(), true);
        }

        public void onMessageDelete(MessageDeleteEvent event) {
            react(event.getMessageId(), false);
        }

        private void react(String id, boolean edit) {
            if (response == null || init == null || init.getId() == null)
                return;
            executor.submit(() -> {
                if (init.getId().equals(id)) {
                    try {
                        if (edit)
                            response.updateMessageAsync("**I don't respond to edited messages!**", m -> {
                                try {
                                    Thread.sleep(3000);
                                    if (m != null)
                                        m.deleteMessage();
                                } catch (InterruptedException | RateLimitedException ignored) {
                                }
                            });
                        else
                            response.updateMessageAsync("**I don't respond to deleted messages!**", m -> {
                                try {
                                    Thread.sleep(3000);
                                    if (m != null)
                                        m.deleteMessage();
                                } catch (InterruptedException | RateLimitedException ignored) {
                                }
                            });
                    } catch (Exception ignored) {
                    }
                }
            });
        }

        public void setResponse(Message m) {
            if (m == null)
                return;
            response = m;
        }

    }

}
