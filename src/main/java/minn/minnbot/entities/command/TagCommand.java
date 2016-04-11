package minn.minnbot.entities.command;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.Tag;
import minn.minnbot.entities.impl.BlockTag;
import minn.minnbot.entities.impl.TagImpl;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.ShutdownEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
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

public class TagCommand extends ListenerAdapter implements Command {

    private List<Tag> tags;
    private String prefix;
    private Logger logger;

    public TagCommand(String prefix, Logger logger, JDA api) {
        this.prefix = prefix;
        this.logger = logger;
        this.tags = new LinkedList<>();
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

    @Override
    public void onShutdown(ShutdownEvent event) {
        JSONArray arr = jsonfy();
        if (new File("tags").exists())
            new File("tags").delete();
        try {
            Files.write(Paths.get("tags.json"), arr.toString(4).getBytes());
        } catch (IOException e) {
            logger.logError(e);
        }
    }

    private JSONArray jsonfy() {
        JSONArray arr = new JSONArray();
        for (Tag t : tags) {
            if (t instanceof BlockTag)
                continue;
            JSONObject obj = new JSONObject();
            obj.put("name", t.name());
            obj.put("guild", t.getGuild().getId());
            obj.put("response", t.response());
            obj.put("owner", t.getOwner().getId());
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
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void setLogger(Logger logger) {
        if (logger == null)
            throw new IllegalArgumentException("Logger cannot be null.");
        this.logger = logger;
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
                event.sendMessage("Successfully edited tag!");
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
                if (event.event.getAuthor() != target.getOwner() && !PermissionUtil.checkPermission(event.event.getAuthor(), Permission.MANAGE_ROLES, event.event.getGuild())) {
                    event.sendMessage("You are not authorized to edit this tag.");
                    return;
                }
                tags.remove(target);
                event.sendMessage("Deleted tag!");
                return;
            }
            if (method.equalsIgnoreCase("add")) {
                if (event.arguments.length < 3) {
                    event.sendMessage("Syntax error. Missing arguments.");
                    return;
                }
                String tagName = event.arguments[1];
                if(tagName.equalsIgnoreCase("add") || tagName.equalsIgnoreCase("del") || tagName.equalsIgnoreCase("edt")) {
                    event.sendMessage("Tagname `" + tagName + "` is not allowed.");
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
                event.sendMessage("Created tag `"+ t.name() + "`.");
                return;
            }
            String tagName = event.allArguments;
            for (Tag t : tags) {
                if (t.name().equals(tagName)) {
                    target = t;
                    break;
                }
            }
            if (target == null || target.getGuild() != event.event.getGuild()) {
                event.sendMessage("Not a tag.");
                return;
            }
            event.sendMessage(target.response());
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
        return "tag <method*> <tag> <response> `*methods: del, edt, add`";
    }

    @Override
    public String getAlias() {
        return "tag <method> <tag> <response>";
    }

    @Override
    public boolean requiresOwner() {
        return false;
    }


}
