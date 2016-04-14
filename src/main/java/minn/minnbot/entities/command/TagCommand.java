package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.Tag;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.entities.impl.BlockTag;
import minn.minnbot.entities.impl.TagImpl;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.EmoteUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.ShutdownEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.utils.PermissionUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.MalformedParametersException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class TagCommand extends CommandAdapter {

    private List<Tag> tags;
    private User owner;

    public TagCommand(String prefix, Logger logger, JDA api, User pOwner) {
        if(api != null)
            api.addEventListener(this);
        this.prefix = prefix;
        this.logger = logger;
        this.tags = new LinkedList<>();
        this.owner = pOwner;
        tags.add(new BlockTag("del"));
        tags.add(new BlockTag("edt"));
        tags.add(new BlockTag("add"));
        if (new File("tags.json").exists()) {
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
                        logger.logError(new MalformedParametersException("tags.json contains broken objects."));
                    }
                }
            } catch (Exception e) {
                logger.logError(e);
            }
        }
    }

    public void onShutdown(ShutdownEvent event) {
        JSONArray arr = getAsJsonArray();
        if (new File("tags.json").exists())
            new File("tags.json").delete();
        try {
            Files.write(Paths.get("tags.json"), arr.toString(4).getBytes());
            logger.logError(new minn.minnbot.entities.throwable.Info("Tags haven been saved. " + Paths.get("tags.json")));
        } catch (IOException e) {
            logger.logError(e);
        }
    }

    private JSONObject jsonfy(Tag tag) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("name", tag.name());
            obj.put("guild", tag.getGuild().getId());
            obj.put("response", tag.response());
            obj.put("owner", tag.getOwner().getId());
            return obj;
        } catch (Exception e) {
            logger.logError(e);
            return null;
        }
    }

    private JSONArray getAsJsonArray() {
        JSONArray arr = new JSONArray();
        for (Tag t : tags) {
            if (t instanceof BlockTag)
                continue;
            JSONObject obj = jsonfy(t);
            if (obj != null)
                arr.put(obj);
        }
        return arr;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isPrivate())
            return;
        if (isCommand(event.getMessage().getContent())) {
            logger.logCommandUse(event.getMessage());
            onCommand(new CommandEvent(event));
        }
    }

    @Override
    public void onCommand(CommandEvent event) {
        Tag target = null;
        if (event.allArguments.isEmpty()) {
            String s = "**__Guild Tags:__** ";
            boolean moreThanOne = false;
            for (Tag t : tags) {
                if (t.getGuild() == event.event.getGuild()) {
                    s += "`" + t.name() + "` ";
                    moreThanOne = true;
                }
            }
            if (moreThanOne)
                event.sendMessage(s);
            else
                event.sendMessage("No tags found.");
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
                    if (t.name().equals(tagName) && t.getGuild() == event.event.getGuild()) {
                        target = t;
                        break;
                    }
                }
                if (target == null) {
                    event.sendMessage("Not a tag.");
                    return;
                }
                if (event.event.getAuthor() != target.getOwner() && !PermissionUtil.checkPermission(event.event.getAuthor(), Permission.MANAGE_ROLES, event.event.getGuild())) {
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
                    if (t.name().equals(tagName) && t.getGuild() == event.event.getGuild()) {
                        target = t;
                        break;
                    }
                }
                if (target == null) {
                    event.sendMessage("Not a tag.");
                    return;
                }
                if (event.event.getAuthor() != target.getOwner() && !PermissionUtil.checkPermission(event.event.getAuthor(), Permission.MANAGE_SERVER, event.event.getGuild())) {
                    event.sendMessage("You are not authorized to edit this tag.");
                    return;
                }
                tags.remove(target);
                event.sendMessage("Deleted tag!");
                return;
            }
            if (method.equalsIgnoreCase("add")) {
                User user = event.event.getAuthor();
                if (!PermissionUtil.checkPermission(user, Permission.MANAGE_SERVER, event.event.getGuild()) && user != owner) {
                    event.sendMessage("You are missing the permission to manage the server. Ask someone with the required permissions to add the tag for you.");
                    return;
                }
                if (event.arguments.length < 3) {
                    event.sendMessage("Syntax error. Missing arguments.");
                    return;
                }
                String tagName = event.arguments[1];
                if (tagName.equalsIgnoreCase("add") || tagName.equalsIgnoreCase("del") || tagName.equalsIgnoreCase("edt") || tagName.equalsIgnoreCase("json")) {
                    event.sendMessage("Tagname `" + tagName + "` is not allowed. " + EmoteUtil.getRngThumbsdowns());
                    return;
                }
                String tagResponse = "";
                for (int i = 2; i < event.arguments.length; i++) {
                    tagResponse += " " + event.arguments[i];
                }
                tagResponse = ((tagResponse.startsWith(" ") && tagResponse.length() > 1) ? tagResponse.substring(1) : tagResponse);
                if (tagName.isEmpty() || tagResponse.isEmpty()) {
                    event.sendMessage("Empty names or responses are not allowed.");
                    return;
                }
                for (Tag t : tags) {
                    if (t.name().equals(tagName) && t.getGuild() == event.event.getGuild()) {
                        target = t;
                        break;
                    }
                }
                if (target != null) {
                    event.sendMessage("Already a tag.");
                    return;
                }
                Tag t = new TagImpl(event.event.getAuthor(), event.event.getGuild(), tagName, tagResponse);
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
                    if (t.name().equals(tagName) && t.getGuild() == event.event.getGuild()) {
                        target = t;
                        break;
                    }
                }
                if (target == null) {
                    event.sendMessage("Not a valid tagname. " + EmoteUtil.getRngThumbsdowns());
                    return;
                }
                JSONObject obj = jsonfy(target);
                if (obj != null) {
                    if (obj.toString(4).length() >= 2000) {
                        event.sendMessage("Unable to print jsonfied tag. Reached charecter limit of 2000." + EmoteUtil.getRngThumbsdowns());
                        return;
                    }
                    event.sendMessage("```JSON\n" + obj.toString(4) + "```");
                } else
                    event.sendMessage("Unable to jsonfy given tag. " + EmoteUtil.getRngThumbsdowns());
                return;
            }
            String tagName = event.allArguments;
            for (Tag t : tags) {
                if (t.name().equals(tagName) && t.getGuild() == event.event.getGuild()) {
                    target = t;
                    break;
                }
            }
            if (target == null || target.getGuild() != event.event.getGuild()) {
                event.sendMessage("Not a tag.");
                return;
            }
            event.sendMessage("\u0001 " + target.response());
        } catch (Exception e) {
            logger.logError(e);
        }
    }

    @Override
    public boolean isCommand(String message) {
        try {
            message = message.toLowerCase();
            if (!message.startsWith(prefix))
                return false;
            message = message.substring(prefix.length());
            String command = message.split(" ", 2)[0];
            if (command.equalsIgnoreCase("tag"))
                return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    public String usage() {
        return "tag <method> <tag> <response> " +
                "\nMethods:" +
                "\n> `del` - delete tag." +
                "\n> `edt` - edit tag." +
                "\n> `add` - add new tag." +
                "\n> `json` - print tag as a json object.";
    }

    @Override
    public String getAlias() {
        return "tag <method> <tag> <response>";
    }

}
