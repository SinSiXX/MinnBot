package minn.minnbot.manager;

import minn.minnbot.util.DeleteUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.InviteReceivedEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberBanEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberUnbanEvent;
import net.dv8tion.jda.exceptions.PermissionException;
import net.dv8tion.jda.exceptions.VerificationLevelException;
import net.dv8tion.jda.hooks.ListenerAdapter;

import java.util.LinkedList;
import java.util.List;

public class ModLogManager extends ListenerAdapter {

    public ModLogManager(JDA api) {
        api.addEventListener(this);
    }

    private void log(String s, Guild guild) {
        TextChannel log =
                guild.getTextChannels().parallelStream().filter(c -> c.getName().equalsIgnoreCase("mb-mod-log")).findFirst().orElse(null);
        if (log == null)
            return;
        try {
            log.sendMessageAsync(s.replace("@", "\u0001@\u0001"), null);
        } catch (PermissionException | VerificationLevelException ignored) {
        }
    }

    public void onGuildMemberBan(GuildMemberBanEvent event) {
        String s;
        if (event.getUser() != null)
            s = String.format("**__Banned:__ %s#%s**", event.getUser().getUsername().replace("**", ""), event.getUser().getDiscriminator());
        else
            s = "**__Banned:__ unknown user**";
        log(s, event.getGuild());
    }

    public void onGuildMemberUnban(GuildMemberUnbanEvent event) {
        if (event.getUserName() != null && event.getUserDiscriminator() != null)
            log(String.format("**__Unbanned:__ %s#%s**", event.getUserName().replace("**", ""), event.getUserDiscriminator()), event.getGuild());
        else
            log("**__Unbanned:__ unknown user**", event.getGuild());
    }

    public void onGuildMemberNickChange(GuildMemberNickChangeEvent event) {
        if (event.getNewNick() != null)
            log(String.format("User's nickname changed! **%s** -> **%s**", (event.getPrevNick() == null ? event.getUser().getUsername() : event.getPrevNick()), event.getNewNick()), event.getGuild());
        else if (event.getPrevNick() != null)
            log(String.format("User's nickname reset! **%s** -> **%s**", event.getPrevNick(), event.getUser().getUsername()), event.getGuild());
    }

    public void onInviteReceived(InviteReceivedEvent event) {
        if (event.isPrivate())
            return;
        TextChannel chan = (TextChannel) event.getMessage().getChannel();
        if (chan == null)
            return;
        Role r = chan.getGuild().getRolesForUser(event.getJDA().getSelfInfo()).stream().filter(role -> role.getName().equalsIgnoreCase("StahpDozAds")).findFirst().orElse(null);
        if (r == null || !chan.checkPermission(event.getJDA().getSelfInfo(), Permission.MESSAGE_MANAGE) || !chan.checkPermission(event.getJDA().getSelfInfo(), Permission.MESSAGE_WRITE) || !chan.getGuild().getRolesForUser(event.getAuthor()).isEmpty())
            return;
        List<Message> messageList = new LinkedList<>();
        messageList.add(event.getMessage());
        DeleteUtil.deleteIn(messageList, chan, null);
        chan.sendMessageAsync(event.getAuthor().getAsMention() + ", please send invites to users directly through a **direct message** instead of **advertising** in this channel!", null);
        log(String.format("Deleted Message of **%s#%s**, used invite url!", event.getAuthor().getUsername(), event.getAuthor().getDiscriminator()), chan.getGuild());
    }


}
