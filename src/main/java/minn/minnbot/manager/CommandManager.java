package minn.minnbot.manager;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.throwable.Info;
import minn.minnbot.util.IgnoreUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
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
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CommandManager extends ListenerAdapter {

    private List<Command> commands = new LinkedList<>();
    private List<CmdManager> managers = new LinkedList<>();
    @SuppressWarnings("unused")
    private JDA api;
    private String owner;
    private Logger logger;
    private ThreadPoolExecutor executor;
    private static Map<String, List<String>> prefixMap;

    public static List<String> getPrefixList(String id) {
        if (prefixMap.containsKey(id))
            return prefixMap.get(id);
        else {
            List<String> prefixList = new LinkedList<>();
            prefixMap.put(id, prefixList);
            return prefixList;
        }
    }

    public static boolean addPrefix(Guild guild, String fix) {
        String id = guild.getId();
        List<String> prefixList = getPrefixList(id);
        if (prefixList.contains(fix))
            return false;
        prefixList.add(fix);
        return true;
    }

    public static boolean removePrefix(Guild guild, String fix) {
        String id = guild.getId();
        List<String> prefixList = getPrefixList(id);
        if (!prefixList.contains(fix))
            return false;
        prefixList.remove(fix);
        if (prefixList.isEmpty())
            prefixMap.remove(id);
        return true;
    }

    private void readMap() {
        prefixMap = new HashMap<>();
        File f = new File("prefix.json");
        if (!f.exists()) {
            logger.logThrowable(new Info("prefix.json does not exist."));
            return;
        }
        try {
            JSONArray arr = new JSONArray(Files.readAllBytes(Paths.get("prefix.json")));
            arr.forEach(obj -> {

                JSONObject jObj = (JSONObject) obj;
                String id = jObj.getString("id");

                JSONArray list = jObj.getJSONArray("prefix");

                List<String> prefixList = new LinkedList<>();
                list.forEach(o -> prefixList.add(o.toString()));

                prefixMap.put(id, prefixList);
            });
        } catch (IOException e) {
            logger.logThrowable(e);
        }
    }

    public CommandManager(JDA api, Logger logger, String owner) {
        this.api = api;
        this.logger = logger;
        this.owner = owner;
        this.executor = new ThreadPoolExecutor(10, 30, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), r -> {
            final Thread thread = new Thread(r, "CommandExecution-Thread");
            thread.setPriority(Thread.NORM_PRIORITY);
            thread.setDaemon(true);
            return thread;
        });
        executor.submit(this::readMap);
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
        savePrefixMap();
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || IgnoreUtil.isIgnored(event.getAuthor(), event.getGuild(), event.getTextChannel()))
            return;
        executor.submit(() -> {
            Thread.currentThread().setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) logger);
            for (Command c : commands) {
                if (c.requiresOwner()) {
                    if (event.getAuthor().getId().equals(owner))
                        c.onMessageReceived(event);
                    continue;
                }
                c.onMessageReceived(event);
            }
            managers.parallelStream().forEachOrdered(manager -> {
                if (manager.requiresOwner() && !event.getAuthor().getId().equals(owner))
                    return;
                manager.call(event);
            });
        });
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

    public void savePrefixMap() {
        try {
            save();
        } catch (IOException e) {
            logger.logThrowable(e);
            return;
        }
        logger.logThrowable(new Info("Saved Prefix Map: prefix.json"));
    }

    public static void save() throws IOException {
        JSONArray arr = new JSONArray();

        prefixMap.forEach((id, list) -> {
            if (list.isEmpty()) return;
            JSONObject obj = new JSONObject();
            JSONArray a = new JSONArray();
            list.parallelStream().forEachOrdered(a::put);
            obj.put(id, a);
            arr.put(obj);
        });
        //if (new File("prefix.json").createNewFile())
        Files.write(Paths.get("prefix.json"), arr.toString(4).getBytes());
        /*else*/
        // throw new UnexpectedException("Manager was unable to save prefixMap.");

    }

}
