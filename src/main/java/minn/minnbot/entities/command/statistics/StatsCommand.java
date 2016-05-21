package minn.minnbot.entities.command.statistics;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import minn.minnbot.util.TimeUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.VoiceStatus;
import net.dv8tion.jda.events.message.MessageDeleteEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StatsCommand extends CommandAdapter {

    private boolean running = false;
    private String about;
    private long ping;
    private ThreadPoolExecutor executor;
    private Map<String, Integer> counters = new HashMap<>();

    public StatsCommand(Logger logger, String prefix, String about) {
        this.logger = logger;
        this.prefix = prefix;
        this.about = about;
        executor = new ThreadPoolExecutor(1, 10, 5L, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), r -> {
            final Thread thread = new Thread(r, "StatsExecution-Thread");
            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        });
    }

    @Override
    public void onCommand(CommandEvent event) {
        executor.submit(() -> {
            final String[] stats = new String[1];
            try {
                stats[0] = stats(event, -1);
            } catch (IOException e) {
                running = false;
                logger.logThrowable(e);
                return;
            }
            long start = System.currentTimeMillis();
            event.sendMessage(stats[0], m -> {
                if (m == null)
                    return;
                counters.put(m.getId(), 0);
                try {
                    long finalStart = System.currentTimeMillis();
                    m.updateMessageAsync(stats[0].replace("{ping}", "" + (System.currentTimeMillis() - start)), m1 -> {
                        if (m1 == null)
                            return;
                        ping = System.currentTimeMillis() - finalStart;
                        ThreadPoolExecutor exec = new ThreadPoolExecutor(1, 1, 10L, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), r -> {
                            Thread t = new Thread(r, "Checked");
                            t.setDaemon(true);
                            return t;
                        });
                        exec.submit(() -> called(m1, event, new Checker(m1.getId(), event.jda, exec)));

                    });
                } catch (NullPointerException ignored) {
                }
            });
        });
    }


    private String stats(CommandEvent event, long ms) throws IOException {
        long[] stats = logger.getNumbers();
        JDA api = event.jda;
        /* Ping */
        String ping = (ms < 1) ? "[Ping]({ping})" : String.format("[Ping](%d)", ms);
        /* Mess */
        String messages = String.format("[Messages](%d)", stats[0]);
        /* Comm */
        String commands = String.format("[Commands](%d)\n[Most-Used]: %s", stats[1], logger.mostUsedCommand());
        /* Evnt */
        String events = String.format("[Events](%d)", stats[2]);
        /* Priv */
        String privateMessages = String.format("[Private-Messages](%d)", stats[3]);
        /* Gild */
        String guildMessages = String.format("[Guild-Messages](%d)", stats[4]);
        /* Glds */
        String guilds = String.format("[Servers](%d)", api.getGuilds().size());
        /* Usrs */
        String users = String.format("[Users](%d)", api.getUsers().size());
        /* Chns */
        String channels = String.format("[Channels]: [Private](%d) [Text](%d) [Voice](%d)", api.getPrivateChannels().size(), api.getTextChannels().size(), api.getVoiceChannels().size());
		/* Uptm */
        String uptime = String.format("[Uptime](%s)", TimeUtil.uptime(stats[5]));
		/* mems */
        String mem = String.format("[Memory](%dMB / %dMB)", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L, Runtime.getRuntime().totalMemory() / 1048576L);
		/* Rsps */
        String responses = String.format("[Responses](%d)", api.getResponseTotal());
        /* Thrd */
        String threads = String.format("[Threads](%d)", Thread.activeCount());

        /* Conn */
        int sizeChannels = getConnectedChannelSize(api);
        int sizePlayers = MinnAudioManager.getPlayers().size();
        int queuedSongs = MinnAudioManager.queuedSongs();
        String connections = "Connected to **"
                + sizeChannels + "** voice channel" + ((sizeChannels == 1) ? "" : "s")
                + " with **" + sizePlayers + "** distinct player" + ((sizePlayers != 1) ? "s" : "") + " running and **" + queuedSongs + "** queued Song" + ((queuedSongs != 1) ? "s" : "") + "!";

        return "```md\n" +
                "Statistics: " + about + "\n\n[Connection]:\n" + uptime + "\n" + mem
                + "\n" + ping + "\n\n[Messages]:\n" + messages + "\n" + privateMessages + "\n" + guildMessages
                + "\n\n[Usages]:\n" + commands + "\n" + responses + "\n" + events + "\n\n[Entities]:\n" + threads + "\n" + guilds + "\n" + users + "\n"
                + channels + "``` " + connections + "";
    }

    private int getConnectedChannelSize(JDA api) {
        List<Guild> connected = new LinkedList<>();
        api.getGuilds().parallelStream().filter(guild -> {
            VoiceStatus status = guild.getVoiceStatusOfUser(api.getSelfInfo());
            return status != null && status.getChannel() != null;
        }).forEach(connected::add);
        return connected.size();
    }

    public String getAlias() {
        return "stats";
    }

    @Override
    public String example() {
        return "stats";
    }

    private void called(Message m, CommandEvent event, Checker checker) {
        if (m == null || event == null || !counters.containsKey(m.getId()))
            return;
        try {
            int counter = counters.get(m.getId());
            if (counter == 10) {
                counters.remove(m.getId());
                return;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
                return;
            }
            String stats;
            try {
                stats = stats(event, ping); // GET CONTENT
            } catch (IOException e) {
                logger.logThrowable(e); // SOMETHING BROKE?
                return;
            }
            long finalStart2 = System.currentTimeMillis();
            m.updateMessageAsync(stats, ms -> {
                ping = System.currentTimeMillis() - finalStart2;
                if (ms == null) {
                    counters.remove(m.getId());
                    return;
                }
                counters.put(m.getId(), counter + 1);
                called(ms, event, checker);
            });
        } catch (NullPointerException ignored) {
            counters.remove(m.getId());
        }
    }

    private class Checker extends ListenerAdapter {

        private String messageId;
        private Thread keepAlive;
        private ThreadPoolExecutor toStop;

        Checker(String messageId, JDA api, ThreadPoolExecutor toStop) {
            if (messageId == null || messageId.isEmpty() || api == null || toStop == null)
                throw new IllegalArgumentException("MessageId was null or empty!");
            this.toStop = toStop;
            this.messageId = messageId;
            keepAlive = new Thread(() -> {
                api.addEventListener(this);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ignored) {
                }
                api.removeEventListener(this);
            });
            keepAlive.setDaemon(true);
            keepAlive.setName("Message exists?");
            keepAlive.setPriority(1);
            keepAlive.start();
        }

        public void onMessageDelete(MessageDeleteEvent event) {
            if (event.getMessageId().equals(messageId)) {
                counters.remove(messageId);
                toStop.shutdownNow();
                if (keepAlive.isAlive()) {
                    keepAlive.interrupt();
                }
            }
        }
    }

}
