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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
    private static PrefixWriter writer;
    private static boolean reading = true;

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
        }
        try {
            JSONArray arr = new JSONArray(new String(Files.readAllBytes(Paths.get("prefix.json"))));
            for (Object anArr : arr)
                try {
                    JSONObject jObj = (JSONObject) anArr;
                    String id = jObj.keys().next();

                    JSONArray list = jObj.getJSONArray(id);
                    List<String> prefixList = new LinkedList<>();

                    for (Object aFix : list) prefixList.add(aFix.toString());

                    prefixMap.put(id, prefixList);
                } catch (Exception ex) {
                    logger.logThrowable(ex);
                }
        } catch (IOException e) {
            logger.logThrowable(new Info("prefix.json does not exist."));
        } catch (JSONException e) {
            logger.logThrowable(new Info("prefix array is malformed!"));
        }
        reading = false;
    }

    public CommandManager(Logger logger, String owner) {
        this.logger = logger;
        this.owner = owner;
        this.executor = new ThreadPoolExecutor(50, 60, 5L, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), r -> {
            final Thread thread = new Thread(r, "CommandExecution-Thread");
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.setDaemon(true);
            return thread;
        });
        executor.submit(this::readMap);
        writer = new PrefixWriter();
    }

    public void setApi(JDA api) {
        this.api = api;
        if(!api.getRegisteredListeners().contains(this)) api.addEventListener(this);
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
            //noinspection deprecation
            Thread.currentThread().stop();
        });
    }

    public void registerManager(CmdManager manager) throws UnsupportedDataTypeException {
        if (manager == null) throw new UnsupportedDataTypeException("Manager is null!");
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
        if (reading) {
            logger.logThrowable(new Info("Tried to save prefix map before it finished reading!"));
            return;
        }
        try {
            save();
        } catch (IOException e) {
            logger.logThrowable(e);
            return;
        }
        logger.logThrowable(new Info("Saved Prefix Map: prefix.json"));
    }

    public static void save() throws IOException {
        if (reading) {
            new Info("Tried to save prefix map before it finished reading!").printStackTrace();
            return;
        }
        JSONArray arr = new JSONArray();

        prefixMap.forEach((id, list) -> {
            if (list.isEmpty()) return;
            JSONObject obj = new JSONObject();
            JSONArray a = new JSONArray();
            list.parallelStream().forEachOrdered(a::put);
            obj.put(id, a);
            arr.put(obj);
        });
        writer.write(arr.toString(4));
    }

    private class PrefixWriter {
        private File f;
        private BufferedOutputStream stream;

        PrefixWriter() {
            f = new File("prefix.json");
            try {
                if (!f.exists())
                    f.createNewFile();
                stream = new BufferedOutputStream(new FileOutputStream(f));
            } catch (IOException e) {
                logger.logThrowable(e);
            }
        }

        void writeln(String input) {
            if(reading)
                return;
            try {
                stream.write((input + "\n").getBytes());
            } catch (IOException e) {
                logger.logThrowable(e);
            }
        }

        void write(String input) {
            if(reading)
                return;
            try {
                stream.write(input.getBytes());
            } catch (IOException e) {
                logger.logThrowable(e);
            }
        }

        void close() {
            try {
                stream.close();
            } catch (IOException e) {
                logger.logThrowable(e);
            }
        }
    }

}
