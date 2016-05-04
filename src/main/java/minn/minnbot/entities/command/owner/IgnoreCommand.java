package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.EmoteUtil;
import minn.minnbot.util.IgnoreUtil;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class IgnoreCommand extends CommandAdapter {

    public IgnoreCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.allArguments.isEmpty()) {
            event.sendMessage(IgnoreUtil.listAll());
            return;
        }
        String method = event.arguments[0];
        if (method.equalsIgnoreCase("guild")) {
            try {
                String id;
                Guild g = null;
                try {
                    id = "" + Long.parseLong(event.allArguments);
                    g = event.event.getJDA().getGuildById(id);
                } catch (NumberFormatException ignored) {

                }
                if (g == null) {
                    List<Guild> matches = event.event.getJDA().getGuilds().stream().filter(guild -> guild.getName().equalsIgnoreCase(event.allArguments)).collect(Collectors.toCollection(LinkedList::new));
                    if(matches.size() > 1) {
                        event.sendMessage("Guilds that match: ```java\n" + matches.toString() + "```");
                        return;
                    } else if(matches.isEmpty()) {
                        event.sendMessage("No guilds that match. " + EmoteUtil.getRngThumbsdown());
                        return;
                    }
                    g = matches.get(0);
                }
                if(IgnoreUtil.toggleIgnore(g)) {
                  event.sendMessage("Now ignoring given guild. `" + g.getName() + "` " + EmoteUtil.getRngThumbsup());
                    return;
                }
                event.sendMessage("Stopped ignoring given guild.");
            } catch (UnsupportedOperationException ignored) {
                event.sendMessage("I was unable to find that guild, sorry.");
            }
        } else if (method.equalsIgnoreCase("user")) {
            try {
                if (event.event.getMessage().getMentionedUsers().isEmpty()) {
                    event.sendMessage("Mention the user you want to ignore please.");
                    return;
                }
                User target = event.event.getMessage().getMentionedUsers().get(0);
                if (IgnoreUtil.toggleIgnore(target)) {
                    event.sendMessage("Now ignoring user. " + EmoteUtil.getRngThumbsup());
                } else {
                    event.sendMessage("Stopped ignoring user. " + EmoteUtil.getRngThumbsup());
                }
            } catch (UnsupportedOperationException ignored) {
                event.sendMessage("I don't even know that user wth. " + EmoteUtil.getRngThumbsdown());
            }
        } else if (method.equalsIgnoreCase("channel")) {
            try {
                if (event.event.getMessage().getMentionedChannels().isEmpty()) {
                    if (IgnoreUtil.toggleIgnore(event.event.getTextChannel()))
                        event.sendMessage("Now ignoring this channel. " + EmoteUtil.getRngOkHand());
                } else {
                    if (IgnoreUtil.toggleIgnore(event.event.getMessage().getMentionedChannels().get(0))) {
                        event.sendMessage("Now ignoring that channel. " + EmoteUtil.getRngOkHand());
                    } else {
                        event.sendMessage("Stopped ignoring that channel. " + EmoteUtil.getRngOkHand());
                    }
                }
            } catch (UnsupportedOperationException ignored) {
                event.sendMessage("Not a text channel afaik. " + EmoteUtil.getRngThumbsdown());
            }
        } else {
            event.sendMessage(usage());
        }
    }

    @Override
    public boolean isCommand(String message) {
        String[] p = message.split(" ", 2);
        return p.length > 0 && p[0].equalsIgnoreCase(prefix + "ignore");
    }

    @Override
    public String getAlias() {
        return "ignore <method> <input>";
    }

    @Override
    public String usage() {
        return "Methods: `guild <id>/<name>`, `user <mention>`, `channel <mention>`";
    }

    @Override
    public boolean requiresOwner() {
        return true;
    }

    @Override
    public String example() {
        return "ignore channel #general";
    }

}
