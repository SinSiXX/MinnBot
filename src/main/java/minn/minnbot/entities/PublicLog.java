package minn.minnbot.entities;

import minn.minnbot.util.IgnoreUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
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
    private static BlockingQueue<Entry> queue = new LinkedBlockingDeque<>(10);
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
            if (workingThread != null && !workingThread.isInterrupted() && workingThread.isAlive())
                workingThread.interrupt();
            workingThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Entry entry = queue.poll(1L, TimeUnit.MINUTES);
                        if(entry == null) {
                            Thread.sleep(1500);
                            continue;
                        }
                        logInternal(entry.message);
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

    public static synchronized void log(String s, User u) {
        Entry e = new Entry(s, u);
        if (!enqueue(e))
            checkForSpam(e);
    }

    public static void log(String s) {
        enqueue(new Entry(s, null));
    }

    private static synchronized boolean enqueue(Entry entry) throws IllegalStateException {
        return queue.offer(entry);
    }

    private static synchronized void checkForSpam(Entry u) {
        int count = 0;
        if(u == null || u.user == null)
            return;
        for (Entry e : queue) {
            if (e.user == null || !e.equals(u) || e.enteredAt + 2000 < u.enteredAt)
                break;
            count++;
        }
        if ((count / queue.size()) * 100 > 50) {
            queue.clear();
            IgnoreUtil.ignore(u.user);
            try {
                u.user.getPrivateChannel().sendMessageAsync("**You have been detected by the automated spam filter and are now on the global blacklist. Please request to be removed from it in the development server!**", null);
            } catch (Exception ignored) {
            }
        }

    }

    private static void logUnprotected(String s) {
        if (api != null) {
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
        if (api != null) {
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

    private static class Entry {

        String message;
        User user;
        long enteredAt = System.currentTimeMillis();

        Entry(String string, User user) {
            this.message = string;
            this.user = user;
        }

        long getEnteredAt() {
            return enteredAt;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Entry && ((Entry) other).user == this.user;
        }

    }

}
