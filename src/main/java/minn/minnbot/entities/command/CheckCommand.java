package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.EntityUtil;
import minn.minnbot.util.TimeUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.List;

public class CheckCommand extends CommandAdapter {

    public CheckCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isPrivate())
            super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        java.util.List<User> mentions = event.message.getMentionedUsers();
        User u;
        if (mentions.isEmpty()) {
            if (event.allArguments.isEmpty()) {
                u = event.author;
            } else {
                u = EntityUtil.getUserByNameDisc(event.allArguments, event.guild);
                if (u == null) {
                    List<User> userList = EntityUtil.getUsersByName(event.allArguments, event.event.getTextChannel());
                    if (userList.size() > 1) {
                        StringBuilder builder = new StringBuilder("Found multiple users fitting that name, please be more specific!");

                        String match = event.allArguments.toLowerCase();

                        for (int i = 0; i < userList.size() && i < 6; i++) {
                            String nick = event.guild.getNicknameForUser(userList.get(i));

                            builder.append("\n- :passport_control: ").append( // I hate this so much
                                    (nick != null && event.guild.getNicknameForUser(userList.get(i)).toLowerCase().contains(match))
                                            ? event.guild.getNicknameForUser(userList.get(i)).contains("**")
                                            ? event.guild.getNicknameForUser(userList.get(i))
                                            : event.guild.getNicknameForUser(userList.get(i)).toLowerCase().replace(match, String.format("**%s**", match))
                                            : userList.get(i).getUsername().contains("**")
                                            ? userList.get(i).getUsername()
                                            : userList.get(i).getUsername().toLowerCase().replace(match, String.format("**%s**", match)));

                        }

                        event.sendMessage(builder.toString());
                        return;
                    }
                    if (userList.isEmpty()) {
                        event.sendMessage(String.format("**No username/nickname in this guild contains __%s__!**", event.allArguments));
                        return;
                    } else
                        u = userList.get(0);
                }
            }
        } else
            u = mentions.get(0);
        String s = "```md";
        String name = null;
        if (event.guild != null) name = event.guild.getNicknameForUser(u);
        if (name != null)
            s += ("\n[Nick][" + name + "]").replace("`", "\u0001`");
        s += ("\n[User][" + u.getUsername() + '#' + u.getDiscriminator() + "]").replace("`", "\u0001`");
        s += "\n[ Id ][" + u.getId() + "]";
        s += "\n[Status][" + ((u.getOnlineStatus() != null) ? u.getOnlineStatus().name() : "OFFLINE") + "]";
        s += ("\n\n[Game][" + ((u.getCurrentGame() == null) ? "Ready to play!" : u.getCurrentGame()) + "]").replace("`", "\u0001`");
        s += "\n[Join][" + TimeUtil.getJoinDate(u, event.guild) + "]";
        s += "\n[Creation][" + TimeUtil.getCreationTime(Long.valueOf(u.getId())) + "]";
        s += "\n[Known guilds][" + serversInCommon(u, event.jda) + "]";
        s += "\n\n[Avatar][ " + u.getAvatarUrl() + " ]";
        s += "\n\n" + getRolesForUser(u, event.event.getGuild()).replace("`", "\u0001`");
        event.sendMessage(s + "```");
    }

    private String getRolesForUser(User u, Guild g) {
        if (g == null)
            return "";
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
        return "check Minn#6688";
    }

}
