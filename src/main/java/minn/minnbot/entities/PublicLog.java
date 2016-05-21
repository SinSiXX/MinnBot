package minn.minnbot.entities;

import minn.minnbot.util.IgnoreUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.ShutdownEvent;
import net.dv8tion.jda.exceptions.PermissionException;
import net.dv8tion.jda.exceptions.RateLimitedException;
import net.dv8tion.jda.exceptions.VerificationLevelException;
import net.dv8tion.jda.hooks.ListenerAdapter;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class PublicLog extends ListenerAdapter {

    private static String channelId;
    public final static PublicLog log = new PublicLog();
    private static JDA api;
    private static BlockingQueue<Entry> queue = new LinkedBlockingDeque<>(10);
    private static Thread workingThread;
    private static Consumer<Throwable> consumer;
    private static String ownerId = "";

    public static PublicLog init(JDA api, Consumer<Throwable> callback) {
        PublicLog.consumer = callback;
        Thread t = new Thread(() -> {
            String location = "Log.json";
            File f = new File(location);
            if (f.exists()) {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(new String(Files.readAllBytes(Paths.get(f.getCanonicalPath()))));
                    if (obj.has("public-log")) channelId = obj.getString("public-log");
                    if (obj.has("owner")) ownerId = obj.getString("owner");
                    setApi(api);
                } catch (IOException e) {
                    callback.accept(e);
                }
            }
            if (workingThread != null && !workingThread.isInterrupted() && workingThread.isAlive())
                workingThread.interrupt();
            workingThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Entry entry;
                        try {
                            entry = queue.poll(1L, TimeUnit.MINUTES);
                        } catch (InterruptedException e) {
                            Thread.sleep(2000);
                            continue;
                        }
                        if (entry == null) {
                            Thread.sleep(2000);
                            continue;
                        }
                        logInternal(entry.message);
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        callback.accept(e);
                    }
                }
            });
            workingThread.setPriority(3);
            workingThread.setDaemon(true);
            workingThread.setName("PublicLog-WorkingThread");
            workingThread.start();
            workingThread.setUncaughtExceptionHandler(((t1, e) -> logInternal("**Public Log was stopped: " + e.getMessage() + "**")));
        });
        t.setDaemon(true);
        t.start();
        t.setUncaughtExceptionHandler((t1, e) -> callback.accept(e));
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

    public void onShutdown(ShutdownEvent event) {
        workingThread.interrupt();
    }

    public static void log(String s, User u) {
        Entry e = new Entry(s, u);
        if (!enqueue(e))
            checkForSpam(e);
    }

    public static void log(String s) {
        enqueue(new Entry(s, null));
    }

    private static boolean enqueue(Entry entry) throws IllegalStateException {
        return queue.offer(entry);
    }

    private static synchronized void checkForSpam(Entry u) {

        Thread t = new Thread(() -> {
            int count = 0;
            if (u == null || u.user == null) return;
            if (Objects.equals(u.user.getId(), ownerId)) return;
            for (Entry e : queue) {
                if (e == null || e.user == null || !e.equals(u) || e.enteredAt + 10000 < u.enteredAt) break;
                count++;
            }
            if ((count / queue.size()) * 100 >= 50) {
                queue.clear();
                IgnoreUtil.ignore(u.user);
                try {
                    u.user.getPrivateChannel().sendMessageAsync("**You have been detected by the automated spam filter and are now on the global blacklist. Please request to be removed from it in the development server!**\nhttps://discord.gg/0mcttggeFpaqAWLI", null);
                } catch (Exception ignored) {
                }
            }
        });
        t.setDaemon(true);
        t.setPriority(10);
        t.setName("Spam-protection");
        t.start();
    }

    private static void logInternal(String s) {
        try {
            if (api != null) {
                TextChannel channel = api.getTextChannelById(channelId);
                if (s != null && !s.isEmpty() && channel != null && channel.checkPermission(api.getSelfInfo(), Permission.MESSAGE_WRITE) && s.length() < 2000) {
                    if (!channel.getGuild().isAvailable())
                        return;
                    try {
                        channel.sendMessageAsync(s.replace("@", "\u0001@\u0001"), null);
                    } catch (VerificationLevelException | RateLimitedException | PermissionException ignored) {
                    }
                }
            }
        } catch (Exception e) {
            consumer.accept(e);
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

        @Override
        public boolean equals(Object other) {
            return other instanceof Entry && ((Entry) other).user == this.user;
        }

    }

}
