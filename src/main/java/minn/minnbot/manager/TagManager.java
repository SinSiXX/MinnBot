package minn.minnbot.manager;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.Tag;
import minn.minnbot.entities.impl.BlockTag;
import net.dv8tion.jda.events.ShutdownEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TagManager extends ListenerAdapter {

    private List<Tag> tags;
    private static TagManager manager;
    private Logger logger;
    private Thread autoSave;

    public void onShutdown(ShutdownEvent event) {
        saveTags();
    }

    public List<Tag> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public static JSONObject jsonfy(Tag tag) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("name", tag.name());
            obj.put("guild", tag.getGuild().getId());
            obj.put("response", tag.response());
            obj.put("owner", tag.getOwner().getId());
            return obj;
        } catch (Exception e) {
            manager.logger.logThrowable(e);
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

    static void saveTags() {
        JSONArray arr = manager.getAsJsonArray();
        if (new File("tags.json").exists())
            //noinspection ResultOfMethodCallIgnored
            new File("tags.json").delete();
        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("tags.json"), "UTF-8"));
            try {
                out.write(arr.toString(4));
            } finally {
                //noinspection ThrowFromFinallyBlock
                out.close();
            }
            // Files.write(Paths.get("tags.json"), arr.toString(4).getBytes());
            manager.logger.logThrowable(new minn.minnbot.entities.throwable.Info("Tags haven been saved. " + Paths.get("tags.json")));
        } catch (IOException e) {
            manager.logger.logThrowable(e);
        }
    }

    public TagManager(List<Tag> tags, Logger logger) {
        this.logger = logger;
        this.tags = tags;
        manager = this;
        if(autoSave != null)
            autoSave.interrupt();
        autoSave = new Thread(() -> {
            while (!autoSave.isInterrupted()) {
                try {
                    Thread.sleep(TimeUnit.HOURS.toMillis(1L));
                } catch (InterruptedException e) {
                    break;
                }
                saveTags();
                try {
                    CommandManager.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        autoSave.setDaemon(true);
        autoSave.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) logger);
        autoSave.setName("TagAutoSave-Thread");
        autoSave.start();
    }

}
