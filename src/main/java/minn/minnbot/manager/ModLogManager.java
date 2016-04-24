package minn.minnbot.manager;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.guild.member.GuildMemberBanEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberUnbanEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class ModLogManager extends ListenerAdapter {

    public ModLogManager(JDA api) {
        api.addEventListener(this);
    }

    public void onGuildMemberBan(GuildMemberBanEvent event) {
        Guild guild = event.getGuild();
        User user = event.getUser();
        TextChannel log = null;
        for (TextChannel c : guild.getTextChannels()) {
            if (c.getName().equalsIgnoreCase("mb-mod-log")) {
                log = c;
                break;
            }
        }
        if (log == null)
            return;
        if (user != null)
            log.sendMessageAsync("**__Banned:__ " + user.getUsername().replace("**", "") + "#" + user.getDiscriminator() + "**", null);
        else
            log.sendMessageAsync("**__Banned:__ " + "unknown user" + "**", null);
    }

    public void onGuildMemberUnban(GuildMemberUnbanEvent event) {
        Guild guild = event.getGuild();
        User user = event.getJDA().getUserById(event.getUserId());
        TextChannel log = null;
        for (TextChannel c : guild.getTextChannels()) {
            if (c.getName().equalsIgnoreCase("mb-mod-log")) {
                log = c;
                break;
            }
        }
        if (log == null)
            return;
        if (user != null)
            log.sendMessageAsync("**__Unbanned:__ " + user.getUsername().replace("**", "") + "#" + user.getDiscriminator() + "**", null);
        else
            log.sendMessageAsync("**__Unbanned:__ " + "unknown user" + "**", null);
    }

}
