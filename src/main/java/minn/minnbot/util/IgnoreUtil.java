package minn.minnbot.util;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

import java.util.LinkedList;
import java.util.List;

public class IgnoreUtil {

    private static final List<User> users = new LinkedList<>();
    private static final List<Guild> guilds = new LinkedList<>();
    private static final List<TextChannel> channels = new LinkedList<>();

    public static String listAll() {
        return "Guilds: "
                + (listGuilds().isEmpty() ? "none" : listGuilds())
                + "\nTextChannels: "
                + (listChannels().isEmpty() ? "none" : listChannels())
                + "\nUsers: "
                + (listUsers().isEmpty() ? "none" : listUsers());
    }

    public static String listUsers() {
        if(users.isEmpty())
            return "";
        String s = "";
        for(User u : users) {
            s += "`" + u.getUsername().replace("`", "") + "#" + u.getDiscriminator() + "`, ";
        }
        return s;
    }

    public static String listChannels() {
        if(channels.isEmpty())
            return "";
        String s = "";
        for(TextChannel c : channels) {
            s += "`" + c.getName() + "<" + c.getId() + ">`, ";
        }
        return s;
    }

    public static String listGuilds() {
        if(guilds.isEmpty())
            return "";
        String s= "";
        for(Guild g : guilds) {
            s += "`" + g.getName().replace("`", "") + "<" + g.getId() + ">`, ";
        }
        return s;
    }

    public static boolean isIgnored(User user, Guild guild, TextChannel channel) {
        return isIgnored(user) || isIgnored(guild) || isIgnored(channel);
    }

    public static boolean isIgnored(User u) {
        return u != null && users.contains(u);
    }

    public static boolean isIgnored(Guild g) {
        return g != null && guilds.contains(g);
    }

    public static boolean isIgnored(TextChannel c) {
        return c != null && channels.contains(c);
    }

    public static boolean toggleIgnore(User u) {
        if(u == null)
            throw new UnsupportedOperationException("User can not be null!");
        if(users.contains(u)) {
            users.remove(u);
            return false;
        }
        ignore(u);
        return true;
    }

    public static boolean toggleIgnore(Guild g) {
        if(g == null)
            throw new UnsupportedOperationException("Guild can not be null!");
        if(users.contains(g)) {
            users.remove(g);
            return false;
        }
        ignore(g);
        return true;
    }

    public static boolean toggleIgnore(TextChannel c) {
        if(c == null)
            throw new UnsupportedOperationException("TextChannel can not be null!");
        if(users.contains(c)) {
            users.remove(c);
            return false;
        }
        ignore(c);
        return true;
    }

    public static void ignore(User u) {
        if (u == null || users.contains(u))
            return;
        users.add(u);
    }

    public static void ignore(Guild g) {
        if (g == null || guilds.contains(g))
            return;
        guilds.add(g);
    }

    public static void ignore(TextChannel c) {
        if (c == null || channels.contains(c))
            return;
        channels.add(c);
    }
}
