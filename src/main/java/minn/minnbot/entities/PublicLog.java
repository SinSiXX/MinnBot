package minn.minnbot.entities;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.hooks.ListenerAdapter;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class PublicLog extends ListenerAdapter {

    private static String channelId;
    public final static PublicLog log = new PublicLog();
    private static JDA api;
    private static BlockingQueue<String> queue = new LinkedBlockingDeque<>(10);
    private static Thread workingThread;

    public static PublicLog init(JDA api) {
        Thread t = new Thread(() -> {
            String location = "Log.json";
            File f = new File(location);
            if (f.exists()) {
                try {
                    JSONObject obj = new JSONObject(new String(Files.readAllBytes(Paths.get(f.getCanonicalPath()))));
                    if (obj.has("public-log")) {
                        channelId = obj.getString("public-log");
                    }
                    setApi(api);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(workingThread != null && !workingThread.isInterrupted() && workingThread.isAlive()) workingThread.interrupt();
            workingThread = new Thread(() -> {
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        logInternal(queue.poll(1L, TimeUnit.MINUTES));
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
            workingThread.setPriority(3);
            workingThread.setDaemon(true);
            workingThread.setName("PublicLog-WorkingThread");
            workingThread.start();
        });
        t.setDaemon(true);
        t.setUncaughtExceptionHandler((t1, e) -> e.printStackTrace());
        t.start();
        return log;
    }

    private PublicLog() {
    }

    private static PublicLog setApi(JDA api) {
        if (api != null && !api.getRegisteredListeners().contains(log)) {
            api.addEventListener(log);
            PublicLog.api = api;
        }
        return log;
    }

    public static synchronized void log(String s) {
        try {
            queue.add(s);
        } catch (IllegalStateException ignored) {}
    }

    private static void logUnprotected(String s) {
        if(api != null) {
            TextChannel channel = api.getTextChannelById(channelId);
            if (s != null && !s.isEmpty() && channel != null && channel.checkPermission(api.getSelfInfo(), Permission.MESSAGE_WRITE) && s.length() < 2000) {
                channel.sendMessageAsync(s, null);
            }
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {
        }
    }

    private static void logInternal(String s) {
        if(api != null) {
            TextChannel channel = api.getTextChannelById(channelId);
            if (s != null && !s.isEmpty() && channel != null && channel.checkPermission(api.getSelfInfo(), Permission.MESSAGE_WRITE) && s.length() < 2000) {
                channel.sendMessageAsync(s.replace("@", "\u0001@\u0001"), null);
            }
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {
        }
    }

}
