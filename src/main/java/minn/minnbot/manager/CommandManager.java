package minn.minnbot.manager;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.TagCommand;
import minn.minnbot.entities.throwable.Info;
import minn.minnbot.util.IgnoreUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.ShutdownEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.activation.UnsupportedDataTypeException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    private List<Command> commands = new LinkedList<>();
    private List<CmdManager> managers = new LinkedList<>();
    @SuppressWarnings("unused")
    private JDA api;
    private User owner;
    private Logger logger;

    public CommandManager(JDA api, Logger logger, User owner) {
        this.api = api;
        this.logger = logger;
        this.owner = owner;
    }

    public List<Command> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    public List<CmdManager> getManagers() {
        return Collections.unmodifiableList(managers);
    }

    public List<Command> getAllCommands() {
        Thread.currentThread().setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) logger);
        List<Command> cmds = new LinkedList<>();
        cmds.addAll(commands);
        managers.parallelStream().filter(manager -> manager != null && manager.getCommands() != null).forEachOrdered(manager -> cmds.addAll(manager.getCommands()));
        return Collections.unmodifiableList(cmds);
    }

    public void onShutdown(ShutdownEvent event) {
        for (Command c : commands) {
            if (c instanceof TagCommand) {
                ((TagCommand) c).onShutdown(event);
                return;
            }
        }
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || IgnoreUtil.isIgnored(event.getAuthor(), event.getGuild(), event.getTextChannel()))
            return;
        Thread t = new Thread(() -> {
            try {

                for (Command c : commands) {
                    if (c.requiresOwner()) {
                        if (event.getAuthor() == owner)
                            c.onMessageReceived(event);
                        continue;
                    }
                    c.onMessageReceived(event);
                }
                managers.parallelStream().forEachOrdered(manager -> {
                    if(manager.requiresOwner() && event.getAuthor() != owner)
                        return;
                    manager.call(event);
                });

            } catch (Exception e) {
                logger.logThrowable(e);
            }
        });
        t.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) logger);
        t.setName("MessageHandling");
        t.start();
    }

    public void registerManager(CmdManager manager) throws UnsupportedDataTypeException {
        if (manager == null) {
            throw new UnsupportedDataTypeException("Manager is null!");
        }
        managers.add(manager);
    }

    @Deprecated
    public String registerCommand(Command com) {
        try {
            commands.add(com);
        } catch (Exception e) {
            return com.getClass().getSimpleName() + ": " + e;
        }
        return "";
    }

    @SuppressWarnings("unused")
    public boolean removeCommand(Command com) {
        for (Command c : commands) {
            if (com.getAlias().equals(c.getAlias())) {
                commands.remove(c);
                return true;
            }
        }
        return false;
    }

    private JSONObject jsonfy(Command command) {
        String alias = command.getAlias();
        String usage = command.usage();
        boolean ownerOnly = command.requiresOwner();
        JSONObject obj = new JSONObject();
        obj.put("aliase", alias);
        obj.put("usage", usage);
        obj.put("owner", ownerOnly);
        return obj;
    }

    public JSONArray getAsJsonArray() {
        JSONArray array = new JSONArray();
        for (Command c : commands) {
            JSONObject obj = new JSONObject();
            obj.put("name", c.getClass().getSimpleName());
            obj.put("data", jsonfy(c));
            array.put(obj);
        }
        return array;
    }

    public File generateJson(String path) {
        JSONArray array = getAsJsonArray();
        File f = new File(path);
        try {
            Files.write(Paths.get(path), array.toString(4).getBytes());
            logger.logThrowable(new Info("Generated Commands as Json: " + path));
        } catch (JSONException | IOException e) {
            logger.logThrowable(e);
        }
        return f;
    }

    public void saveTags() {
        TagManager.saveTags();
    }

}
