package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.EmoteUtil;
import minn.minnbot.util.EntityUtil;
import minn.minnbot.util.IgnoreUtil;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

import java.util.List;

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
        String[] args = event.allArguments.split("\\s+", 2);
        if (args.length < 2) {
            event.sendMessage("Missing input! `ignore <method> <input>`");
            return;
        }
        String method = args[0];
        String input = args[1];
        if (method.equalsIgnoreCase("guild")) {
            try {
                String id;
                Guild g = null;
                try {
                    id = "" + Long.parseLong(input);
                    g = event.jda.getGuildById(id);
                } catch (NumberFormatException ignored) {
                }
                if (g == null) {
                    List<Guild> matches = EntityUtil.getGuildsByName(input, event.jda);
                    if (matches.size() > 1) {
                        StringBuilder b = new StringBuilder("**Multiple Guilds match! Please be more specific!**");
                        for (int i = 0; i < matches.size() && i < 6; i++) {
                            b.append(String.format("\n-%s(%s)", matches.get(i).getName().toLowerCase().replace(input, String.format("**%s**", input)), matches.get(i).getId()));
                        }
                        event.sendMessage(b.toString());
                        return;
                    } else if (matches.isEmpty()) {
                        event.sendMessage("No guilds that match. " + EmoteUtil.getRngThumbsdown());
                        return;
                    }
                    g = matches.get(0);
                }
                if (IgnoreUtil.toggleIgnore(g)) {
                    event.sendMessage("Now ignoring given guild. `" + g.getName() + "` " + EmoteUtil.getRngThumbsup());
                    return;
                }
                event.sendMessage("Stopped ignoring given guild.");
            } catch (UnsupportedOperationException ignored) {
                event.sendMessage("I was unable to find that guild, sorry.");
            }
        } else if (method.equalsIgnoreCase("user")) {
            try {
                User target;
                if (event.message.getMentionedUsers().isEmpty()) {
                    target = EntityUtil.getUserByNameDisc(input, event.jda);
                } else
                    target = event.message.getMentionedUsers().get(0);
                if (target == null) {
                    event.sendMessage(String.format("I am unable to find **%s**", input));
                    return;
                }
                if (IgnoreUtil.toggleIgnore(target)) {
                    event.sendMessage(String.format("Now ignoring **%s**. " + EmoteUtil.getRngThumbsup(), input));
                } else {
                    event.sendMessage(String.format("Stopped ignoring **%s**. " + EmoteUtil.getRngThumbsup(), input));
                }
            } catch (UnsupportedOperationException ignored) {
                event.sendMessage("I don't even know that user wth. " + EmoteUtil.getRngThumbsdown());
            }
        } else {
            if (method.equalsIgnoreCase("channel")) {
                try {
                    if (event.message.getMentionedChannels().isEmpty()) {
                        List<TextChannel> channels = EntityUtil.getTextChannelsByName(input, event.guild);
                        if (channels.isEmpty()) {
                            event.sendMessage(String.format("No channels match **%s**", input));
                            return;
                        }
                        if (channels.size() > 1) {
                            StringBuilder b = new StringBuilder("**Multiple channels match! Please be more specific!**");
                            for (int i = 0; i < channels.size() && i < 6; i++) {
                                b.append(String.format("\n- %s(%s)", channels.get(i).getName().toLowerCase().replace(input, String.format("**%s**", input)), channels.get(i).getId()));
                            }
                            event.sendMessage(b.toString());
                            return;
                        }
                        if (IgnoreUtil.toggleIgnore(channels.get(0)))
                            event.sendMessage(String.format("**Now ignoring %s!**", channels.get(0).getAsMention()));
                        else
                            event.sendMessage(String.format("**Stopped ignoring %s!**", channels.get(0).getAsMention()));
                    } else {
                        if (IgnoreUtil.toggleIgnore(event.message.getMentionedChannels().get(0)))
                            event.sendMessage(String.format(String.format("**Now ignoring %%s. %s**", EmoteUtil.getRngOkHand()), event.message.getMentionedChannels().get(0).getAsMention()));
                        else
                            event.sendMessage(String.format(String.format("**Stopped ignoring %%s. %s**", EmoteUtil.getRngOkHand()), event.message.getMentionedChannels().get(0).getAsMention()));
                    }
                } catch (UnsupportedOperationException ignored) {
                    event.sendMessage("Not a text channel afaik. " + EmoteUtil.getRngThumbsdown());
                }
            } else {
                event.sendMessage(usage());
            }
        }
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
