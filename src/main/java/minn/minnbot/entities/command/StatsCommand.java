package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.MinnAudioManager;
import minn.minnbot.util.TimeUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.VoiceStatus;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StatsCommand extends CommandAdapter {

    private boolean running = false;
    private String about;
    private long ping;
    private ThreadPoolExecutor executor;

    public StatsCommand(Logger logger, String prefix, String about) {
        this.logger = logger;
        this.prefix = prefix;
        this.about = about;
        executor = new ThreadPoolExecutor(1, 10, 1L, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), r -> {
            final Thread thread = new Thread(r, "StatsExecution-Thread");
            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        });
    }

    @Override
    public void onCommand(CommandEvent event) {
        executor.execute(() -> {
            String msg;
            try {
                msg = stats(event, -1);
            } catch (IOException e) {
                running = false;
                logger.logThrowable(e);
                return;
            }
            long start = System.currentTimeMillis();
            final Message[] m = {event.sendMessageBlocking(msg)};
            try {
                if (m[0] != null) {
                    long finalStart = System.currentTimeMillis();
                    m[0].updateMessageAsync(msg.replace("{ping}", (System.currentTimeMillis() - start) + ""), (ms2) -> {
                        ping = System.currentTimeMillis() - finalStart;
                        m[0] = ms2;
                    });
                    for (int i = 0; i < 10; i++) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ignored) {
                        }
                        try {
                            msg = stats(event, ping);
                        } catch (IOException e) {
                            logger.logThrowable(e);
                            break;
                        }
                        if (m[0] == null)
                            break;
                        long finalStart2 = System.currentTimeMillis();
                        m[0].updateMessageAsync(msg, (ms2) -> {
                            ping = System.currentTimeMillis() - finalStart2;
                            m[0] = ms2;
                        });
                    }
                }
            } catch (NullPointerException ignored) {
            }
        });
    }

    private String stats(CommandEvent event, long ms) throws IOException {
        int[] stats = logger.getNumbers();
        JDA api = event.jda;
        /* Ping */
        String ping = (ms < 1) ? "[Ping][{ping}]" : "[Ping][" + ms + "]";
        /* Mess */
        String messages = "[Messages][" + stats[0] + "]";
        /* Comm */
        String commands = "[Commands][" + stats[1] + "]";
        /* Evnt */
        String events = "[Events][" + stats[2] + "]";
        /* Priv */
        String privateMessages = "[Private-Messages][" + stats[3] + "]";
        /* Gild */
        String guildMessages = "[Guild-Messages][" + stats[4] + "]";
        /* Glds */
        String guilds = "[Servers][" + api.getGuilds().size() + "]";
		/* Usrs */
        String users = "[Users][" + api.getUsers().size() + "]";
		/* Chns */
        String channels = "[Channels]: [Private][" + api.getPrivateChannels().size() + "] [Text][" + api.getTextChannels().size() + "] [Voice]["
                + api.getVoiceChannels().size() + "]";
		/* Uptm */
        String uptime = "[Uptime][" + TimeUtil.uptime(stats[5]) + "]";
		/* mems */
        String mem = "[Memory][" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L
                + "MB / " + Runtime.getRuntime().totalMemory() / 1048576L + "MB]";
		/* Rsps */
        String responses = "[Responses][" + api.getResponseTotal() + "]";
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
                + "\n\n[Usages]:\n" + commands + "\n" + responses + "\n" + events + "\n\n[Entities]:\n" + guilds + "\n" + users + "\n"
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


    @Override
    public boolean isCommand(String message) {
        return message.equalsIgnoreCase(prefix + "stats");
    }

    public String getAlias() {
        return "stats";
    }

    @Override
    public String example() {
        return "stats";
    }
}
