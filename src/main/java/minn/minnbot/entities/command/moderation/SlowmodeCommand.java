package minn.minnbot.entities.command.moderation;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.PermissionOverride;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.ShutdownEvent;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.managers.PermissionOverrideManager;

import java.util.LinkedList;
import java.util.List;

public class SlowmodeCommand extends CommandAdapter {


    private static SlowmodeListener listener = null;

    public SlowmodeCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.isPrivate)
            return;
        if (!((TextChannel) event.channel).checkPermission(event.author, Permission.MANAGE_PERMISSIONS)) {
            event.sendMessage("You need to be able to manage channel permissions to enable/disable slowmode!");
            return;
        }
        if (!((TextChannel) event.channel).checkPermission(event.jda.getSelfInfo(), Permission.MANAGE_PERMISSIONS)) {
            event.sendMessage("I need to be able to manage channel permissions to enable/disable slowmode!");
            return;
        }
        if (listener == null) {
            listener = new SlowmodeListener(event.guild, true);
            event.sendMessage("Slowmode is now **ON**!");
            return;
        }
        if (listener.isSlowed(event.guild)) {
            listener.remove(event.guild);
            event.sendMessage("Slowmode is now **OFF**!");
            return;
        }
        listener.add(event.guild);
        event.sendMessage("Slowmode is now **ON**!");
    }

    @Override
    public String getAlias() {
        return "slowmode";
    }

    private class SlowmodeListener extends ListenerAdapter {

        private boolean on;
        private JDA api;
        List<Instance> instanceList = new LinkedList<>();
        List<String> guildList = new LinkedList<>();

        public SlowmodeListener(Guild guild) {
            this.on = false;
            this.api = guild.getJDA();
            guildList.add(guild.getId());
        }

        public SlowmodeListener(Guild guild, boolean on) {
            this.on = !on;
            guildList.add(guild.getId());
            this.api = guild.getJDA();
            toggle();
        }

        public List<String> getGuilds() {
            return guildList;
        }

        public void add(Guild g, boolean... on) {
            String id = g.getId();
            if (!guildList.contains(id))
                guildList.add(id);
        }

        public void remove(Guild g) {
            String id = g.getId();
            if (!guildList.contains(id)) {
                guildList.remove(id);
                if (guildList.isEmpty()) {
                    api.removeEventListener(this);
                    listener = null;
                }
            }
        }

        public boolean isSlowed(Guild g) {
            return guildList.contains(g.getId());
        }

        public boolean toggle() {
            on = !on;
            if (!on && api.getRegisteredListeners().contains(this)) api.removeEventListener(this);
            else if (on) api.addEventListener(this);
            return on;
        }

        @Deprecated
        public void set(boolean on) {
            this.on = on;
            if (!on && api.getRegisteredListeners().contains(this)) api.removeEventListener(this);
            else if (on) api.addEventListener(this);
        }

        public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
            if (!isSlowed(event.getGuild())
                    || !event.getChannel().checkPermission(event.getJDA().getSelfInfo(), Permission.MANAGE_PERMISSIONS)
                    || api.getSelfInfo() == event.getAuthor()
                    || !event.getChannel().checkPermission(event.getAuthor(), Permission.MANAGE_PERMISSIONS))
                return;
            System.out.println("[DEBUG] TRYING TO SLOW");
            User u = event.getAuthor();
            TextChannel channel = event.getChannel();

            PermissionOverrideManager overrideManager;
            PermissionOverride override = channel.getOverrideForUser(u);
            if (override == null)
                overrideManager = channel.createPermissionOverride(u);
            else
                overrideManager = override.getManager();

            overrideManager.deny(Permission.MESSAGE_WRITE).update();
            Instance i = new Instance(u, channel);
            instanceList.add(i);
            i.start();
            System.out.println("[DEBUG] SLOWED USER");
        }

        public void onShutdown(ShutdownEvent event) {
            instanceList.parallelStream().forEach(Thread::interrupt);
        }

        private class Instance extends Thread {

            private TextChannel channel;
            private User user;

            public Instance(User u, TextChannel channel) {
                this.user = u;
                this.channel = channel;
            }

            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ignored) {
                }
                PermissionOverrideManager overrideManager;
                PermissionOverride override = channel.getOverrideForUser(user);
                if (override == null)
                    overrideManager = channel.createPermissionOverride(user);
                else
                    overrideManager = override.getManager();

                overrideManager.grant(Permission.MESSAGE_WRITE).update();
                instanceList.remove(this);
            }

        }

    }

}
