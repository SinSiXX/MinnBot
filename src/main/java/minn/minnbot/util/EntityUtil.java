package minn.minnbot.util;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

import java.util.List;
import java.util.stream.Collectors;

public class EntityUtil {

    // USERS

    public static User getUserByNameDisc(String combo, Guild guild) {
        String trim = combo.trim();
        return guild.getUsers().parallelStream().filter(user -> (user.getUsername() + "#" + user.getDiscriminator()).equals(trim)).findFirst().orElse(null);
    }

    public static List<User> getUsersByName(String username, TextChannel channel) {
        String lower = username.toLowerCase();
        List<User> userList = channel.getUsers();
        Guild guild = channel.getGuild();
        return userList.parallelStream().filter(u -> {
            String nick = guild.getNicknameForUser(u);
            if (nick != null)
                return guild.getNicknameForUser(u).toLowerCase().contains(lower) || u.getUsername().toLowerCase().contains(lower);
            return u.getUsername().toLowerCase().contains(lower);
        }).collect(Collectors.toList());
    }

    public static User getUserByNameDisc(String combo, JDA api) {
        String trim = combo.trim();
        return api.getUsers().parallelStream().filter(user -> (user.getUsername() + "#" + user.getDiscriminator()).equals(trim)).findFirst().orElse(null);
    }


    // GUILDS

    public static List<Guild> getGuildsByName(String name, JDA api) {
        String n = name.toLowerCase();
        return api.getGuilds().parallelStream().filter(g -> g.getName().toLowerCase().contains(n)).collect(Collectors.toList());
    }


    // Channels

    public static List<TextChannel> getTextChannelsByName(String name, Guild guild) {
        String n = name.toLowerCase();
        return guild.getTextChannels().parallelStream().filter(g -> g.getName().toLowerCase().contains(n)).collect(Collectors.toList());
    }

}
