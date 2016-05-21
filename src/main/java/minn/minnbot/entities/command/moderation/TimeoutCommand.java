package minn.minnbot.entities.command.moderation;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.PermissionOverride;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.DisconnectEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.managers.PermissionOverrideManager;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeoutCommand extends CommandAdapter {

    private List<Thread> timeouts = new LinkedList<>();

    private class Listener extends ListenerAdapter {

        public void onDisconnect(DisconnectEvent event) {
            timeouts.stream().forEach(Thread::interrupt);
            timeouts.clear();
        }

    }

    public TimeoutCommand(String prefix, Logger logger, JDA api) {
        init(prefix, logger);
        api.addEventListener(new Listener());
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.isPrivate)
            return;
        if (!((TextChannel) event.channel).checkPermission(event.author, Permission.MANAGE_PERMISSIONS)) {
            event.sendMessage("You are unable to time users out!");
            return;
        } else if (!((TextChannel) event.channel).checkPermission(event.jda
                .getSelfInfo(), Permission.MANAGE_PERMISSIONS)) {
            event.sendMessage("I am unable to time users out! Missing channel permission: **MANAGE_PERMISSIONS**");
            return;
        }
        List<User> mentions = event.message.getMentionedUsers();
        if (mentions.isEmpty()) {
            event.sendMessage("You have to mention a user to time him out!");
            return;
        }
        Thread to = new Timeout(mentions.get(0), (TextChannel) event.channel);
        timeouts.add(to);
        to.start();
        event.sendMessage(mentions.get(0).getAsMention() + ", you have been timed out! (For 10 minutes)");
    }

    @Override
    public String getAlias() {
        return "timeout <mention>";
    }

    public String example() {
        return "timeout @MinnBot";
    }

    private class Timeout extends Thread {

        private String userId;
        private String chanId;
        private JDA api;

        Timeout(User u, TextChannel channel) {
            this.userId = u.getId();
            this.chanId = channel.getId();
            this.api = channel.getJDA();
        }

        public void run() {
            if (api.getTextChannelById(chanId).checkPermission(api.getSelfInfo(), Permission.MANAGE_PERMISSIONS)) {
                PermissionOverride override = api.getTextChannelById(chanId).getOverrideForUser(api.getUserById(userId));
                PermissionOverrideManager manager;
                if (override == null)
                    manager = api.getTextChannelById(chanId).createPermissionOverride(api.getUserById(userId));
                else
                    manager = override.getManager();
                manager.deny(Permission.MESSAGE_WRITE).update();
            }
            try {
                Thread.sleep(TimeUnit.MINUTES.toMillis(10L));
            } catch (InterruptedException ignored) {
            }
            if (api.getTextChannelById(chanId).checkPermission(api.getSelfInfo(), Permission.MANAGE_PERMISSIONS)) {
                PermissionOverride override = api.getTextChannelById(chanId).getOverrideForUser(api.getUserById(userId));
                PermissionOverrideManager manager;
                if (override == null)
                    manager = api.getTextChannelById(chanId).createPermissionOverride(api.getUserById(userId));
                else
                    manager = override.getManager();
                manager.grant(Permission.MESSAGE_WRITE).update();
            }
            timeouts.remove(this);
        }

    }

}
