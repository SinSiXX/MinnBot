package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.Tag;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.entities.impl.BlockTag;
import minn.minnbot.entities.impl.TagImpl;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.TagManager;
import minn.minnbot.util.EmoteUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.utils.PermissionUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.MalformedParametersException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class TagCommand extends CommandAdapter {

    private static final List<Tag> tags = new LinkedList<>();
    private String owner;


    public static void initTags(JDA api, Logger logger) {
        if (new File("tags.json").exists()) {
            Thread t = new Thread(() -> {
                try {
                    JSONArray arr = new JSONArray(new String(Files.readAllBytes(Paths.get("tags.json"))));
                    for (Object obj : arr) {
                        try {
                            JSONObject intObj = (JSONObject) obj;
                            String name = intObj.getString("name");
                            String response = intObj.getString("response");
                            String ownerId = intObj.getString("owner");
                            String guildId = intObj.getString("guild");
                            User owner = api.getUserById(ownerId);
                            Guild guild = api.getGuildById(guildId);
                            tags.add(new TagImpl(owner, guild, name, response));
                        } catch (Exception e) {
                            logger.logThrowable(new MalformedParametersException("tags.json contains broken objects."));
                        }
                    }
                } catch (Exception e) {
                    logger.logThrowable(e);
                }
            });
            t.setName("TagReader");
            t.setPriority(1);
            t.setDaemon(true);
            t.start();
        }
    }

    public TagCommand(String prefix, Logger logger, String pOwner) {
        this.prefix = prefix;
        this.logger = logger;
        new TagManager(tags, logger);
        this.owner = pOwner;
        tags.add(new BlockTag("del"));
        tags.add(new BlockTag("edt"));
        tags.add(new BlockTag("add"));
        tags.add(new BlockTag("json"));
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isPrivate())
            return;
        super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        Tag target = null;
        if (event.allArguments.isEmpty()) {
            String s = "**__Guild Tags:__** ";
            boolean moreThanOne = false;
            for (Tag t : tags) {
                if(t.getGuild() == null)
                    continue;
                if (t.getGuild() == event.guild) {
                    s += "`" + t.name() + "` ";
                    moreThanOne = true;
                }
            }
            if (moreThanOne) {
                event.sendMessage(s);
            } else {
                event.sendMessage("No tags found.");
            }
            return;
        }
        try {
            String method = event.arguments[0];
            if (method.equalsIgnoreCase("edt")) {
                if (event.arguments.length < 3) {
                    event.sendMessage("Syntax error. Missing arguments.");
                    return;
                }
                String tagName = event.arguments[1];
                String tagResponse = "";
                for (int i = 2; i < event.arguments.length; i++) {
                    tagResponse += " " + event.arguments[i];
                }
                for (Tag t : tags) {
                    if (t.name().equals(tagName) && t.getGuild() == event.guild) {
                        target = t;
                        break;
                    }
                }
                if (target == null) {
                    event.sendMessage("Not a tag.");
                    return;
                }
                if (event.author != target.getOwner() && !PermissionUtil.checkPermission(event.author, Permission.MANAGE_ROLES, event.guild)) {
                    event.sendMessage("You are not authorized to edit this tag.");
                    return;
                }
                target.setResponse(tagResponse);
                event.sendMessage("Successfully edited tag! " + EmoteUtil.getRngOkHand());
                return;
            }
            if (method.equalsIgnoreCase("del")) {
                String tagName = event.arguments[1];
                for (Tag t : tags) {
                    if (t.name().equals(tagName) && t.getGuild() == event.guild) {
                        target = t;
                        break;
                    }
                }
                if (target == null) {
                    event.sendMessage("Not a tag.");
                    return;
                }
                if (event.author != target.getOwner() && !PermissionUtil.checkPermission(event.author, Permission.MANAGE_SERVER, event.event.getGuild())) {
                    event.sendMessage("You are not authorized to edit this tag.");
                    return;
                }
                tags.remove(target);
                event.sendMessage("Deleted tag!");
                return;
            }
            if (method.equalsIgnoreCase("add")) {
                User user = event.author;
                if (!PermissionUtil.checkPermission(user, Permission.MANAGE_SERVER, event.guild) && !user.getId().equals(owner)) {
                    event.sendMessage("You are missing the permission to manage the server. Ask someone with the required permissions to add the tag for you.");
                    return;
                }
                if (event.arguments.length < 3) {
                    event.sendMessage("Syntax error. Missing arguments.");
                    return;
                }
                String tagName = event.arguments[1];
                if (tagName.equalsIgnoreCase("add") || tagName.equalsIgnoreCase("del") || tagName.equalsIgnoreCase("edt") || tagName.equalsIgnoreCase("json")) {
                    event.sendMessage("Tagname `" + tagName + "` is not allowed. " + EmoteUtil.getRngThumbsdown());
                    return;
                }
                String[] parts = event.allArguments.split("\\s+", 3);
                if(parts.length != 3) {
                    event.sendMessage("Empty names or responses are not allowed.");
                    return;
                }
                String tagResponse = parts[2];
                if (tagName.isEmpty()) {
                    event.sendMessage("Empty names or responses are not allowed.");
                    return;
                }
                for (Tag t : tags) {
                    if (t.name().equals(tagName) && t.getGuild() == event.guild) {
                        target = t;
                        break;
                    }
                }
                if (target != null) {
                    event.sendMessage("Already a tag.");
                    return;
                }
                Tag t = new TagImpl(event.author, event.guild, tagName, tagResponse);
                tags.add(t);
                event.sendMessage("Created tag `" + t.name() + "`. " + EmoteUtil.getRngOkHand());
                return;
            }
            if (method.equalsIgnoreCase("json")) {
                String tagName = event.arguments[1];
                if (tagName.equalsIgnoreCase("add") || tagName.equalsIgnoreCase("del") || tagName.equalsIgnoreCase("edt") || tagName.equalsIgnoreCase("json")) {
                    return;
                }
                for (Tag t : tags) {
                    if (t.name().equals(tagName) && t.getGuild() == event.guild) {
                        target = t;
                        break;
                    }
                }
                if (target == null) {
                    event.sendMessage("Not a valid tagname. " + EmoteUtil.getRngThumbsdown());
                    return;
                }
                JSONObject obj = TagManager.jsonfy(target);
                if (obj != null) {
                    if (obj.toString(4).length() >= 2000) {
                        event.sendMessage("Unable to print jsonfied tag. Reached charecter limit of 2000." + EmoteUtil.getRngThumbsdown());
                        return;
                    }
                    event.sendMessage("```JSON\n" + obj.toString(4) + "```");
                } else
                    event.sendMessage("Unable to jsonfy given tag. " + EmoteUtil.getRngThumbsdown());
                return;
            }
            String tagName = event.arguments[0];
            for (Tag t : tags) {
                if (t.name().equals(tagName) && t.getGuild() == event.guild) {
                    target = t;
                    break;
                }
            }
            if (target == null || target.getGuild() != event.guild) {
                event.sendMessage("Not a tag.");
                return;
            }
            event.sendMessage("\u0001 " + TagManager.scanForVars(target, event));
        } catch (Exception e) {
            logger.logThrowable(e);
        }
    }

    @Override
    public String usage() {
        return "\n***Requires MANAGE_SERVER permission***" +
                "\n**tag <method> <tag> <response> **" +
                "\nMethods:" +
                "\n> `del` - delete tag." +
                "\n> `edt` - edit tag." +
                "\n> `add` - add new tag." +
                "\n> `json` - print tag as a json object." +
                "\n\n**__Tag parameters:__ (Case sensitive)**" +
                "\n\n> `%channel%` - current channel" +
                "\n> `%touser%` - mentions given user" +
                "\n> `%users%` - prints amount of guild users.";
    }

    @Override
    public String getAlias() {
        return "tag <method> <tag> <response>";
    }

    @Override
    public String example() {
        return "tag add tagname some stupid response!";
    }

}
