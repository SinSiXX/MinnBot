package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.TimeUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class CheckCommand extends CommandAdapter {

    public CheckCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (isCommand(event.getMessage().getContent())) {
            logger.logCommandUse(event.getMessage());
            onCommand(new CommandEvent(event));
        }
    }

    @Override
    public void onCommand(CommandEvent event) {
        java.util.List<User> mentions = event.event.getMessage().getMentionedUsers();
        User u;
        if (mentions.isEmpty())
            u = event.event.getAuthor();
        else
            u = mentions.get(0);
        String s = "```md\n";
        s += ("[User][" + u.getUsername() + '#' + u.getDiscriminator() + "]").replace("`", "\u0001`");
        s += "\n[ Id ][" + u.getId() + "]";
        s += "\n[Status][" + ((u.getOnlineStatus() != null) ? u.getOnlineStatus().name() : "OFFLINE") + "]";
        s += ("\n\n[Game][" + ((u.getCurrentGame() == null) ? "Ready to play!" : u.getCurrentGame()) + "]").replace("`", "\u0001`");
        s += "\n[Join][" + getJoinDate(u, event.event.getGuild()) + "]";
        s += "\n[Creation][" + TimeUtil.getCreationTime(Long.valueOf(u.getId())) + "]";
        s += "\n[Known guilds][" + serversInCommon(u, event.event.getJDA()) + "]";
        s += "\n\n[Avatar][ " + u.getAvatarUrl() + " ]";
        s += "\n\n" + getRolesForUser(u, event.event.getGuild()).replace("`", "\u0001`");
        try {
            event.sendMessage(s + "```");
        } catch (Exception e) {
            s = "```md\n";
            s += ("[User][" + u.getUsername() + '#' + u.getDiscriminator() + "]").replace("`", "\u0001`");
            s += "\n[ Id ][" + u.getId() + "]";
            s += "\n[Status][" + ((u.getOnlineStatus() != null) ? u.getOnlineStatus().name() : "OFFLINE") + "]";
            s += "\n\n[Game][" + ((u.getCurrentGame() == null) ? "Ready to play!" : u.getCurrentGame()) + "]";
            s += "\n[Join][" + getJoinDate(u, event.event.getGuild()) + "]";
            s += "\n[Creation][" + TimeUtil.getCreationTime(Long.getLong(u.getId())) + "]";
            s += "\n[Known guilds][" + serversInCommon(u, event.event.getJDA()) + "]";
            s += "\n\n[Avatar][ " + u.getAvatarUrl() + " ]";
            event.sendMessage(s + "```");
        }
    }

    private String getRolesForUser(User u, Guild g) {
        java.util.List<Role> roles = g.getRolesForUser(u);
        if (roles.isEmpty())
            return ""; // TODO
        String s = "[Roles]:";
        for (Role r : roles) {
            if (r.getName().equalsIgnoreCase("@everyone"))
                continue;
            try {
                s += "\n>" + r.getName() + " <" + r.getId() + "> " + "[#" + Integer.toHexString(r.getColor()) + "]";
            } catch (Exception ignored) {

            }
        }
        return s;
    }

    private int serversInCommon(User u, JDA api) {
        try {
            java.util.List<Guild> guilds = api.getGuilds();
            int count = 0;
            for (Guild g : guilds) {
                for (User user : g.getUsers()) {
                    if (user == u) {
                        count++;
                        break;
                    }
                }
            }
            return count;
        } catch (Exception e) {
            logger.logThrowable(e);
            return 1;
        }
    }

    private String getJoinDate(User u, Guild g) {
        java.time.OffsetDateTime time = g.getJoinDateForUser(u);
        int day = time.getDayOfMonth();
        int month = time.getMonthValue();
        int year = time.getYear();

        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();

        return "" + ((day < 10) ? "0" + day : day) + "/" + ((month < 10) ? "0" + month : month) + "/" + year + " | "
                + ((hour < 10) ? "0" + hour : hour) + ":" + ((minute < 10) ? "0" + minute : minute) + ":"
                + ((second < 10) ? "0" + second : second);
    }

    @Override
    public boolean isCommand(String message) {
        try {
            message = message.toLowerCase();
            if (!message.startsWith(prefix))
                return false;
            message = message.substring(prefix.length());
            String command = message.split(" ", 2)[0];
            if (command.equalsIgnoreCase("check"))
                return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    public String usage() {
        return "`check @username`";
    }

    @Override
    public String getAlias() {
        return "check <mention>";
    }

    @Override
    public String example() {
        return "check <@158174948488118272>";
    }

}
