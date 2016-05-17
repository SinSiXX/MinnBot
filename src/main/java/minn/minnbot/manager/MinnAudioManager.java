package minn.minnbot.manager;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.events.ShutdownEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.player.MusicPlayer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class MinnAudioManager extends ListenerAdapter {

    private static Thread keepAliveKeepAlive; // dat name doe

    private static Map<Guild, MusicPlayer> players = new HashMap<>();

    private static Map<MusicPlayer, Thread> keepAliveMap = new HashMap<>();

    private static Map<MusicPlayer, Boolean> isLive = new HashMap<>();

    public MinnAudioManager() {
        init();
    }

    private static void init() {
        if (keepAliveKeepAlive == null || !keepAliveKeepAlive.isAlive()) {
            keepAliveKeepAlive = new Thread(() -> {
                while (!keepAliveKeepAlive.isInterrupted()) {
                    try {
                        Thread.sleep(TimeUnit.MINUTES.toMillis(60L));
                    } catch (InterruptedException e) {
                        break;
                    }
                    Map<MusicPlayer, Thread> toRemove = new HashMap<>();
                    keepAliveMap.forEach((player, thread) -> {
                        if (thread.isInterrupted()) {
                            toRemove.put(player, thread);
                        }
                    });
                    toRemove.forEach((player, thread) -> keepAliveMap.remove(player, thread));
                }
            });
            keepAliveKeepAlive.setDaemon(true);
            keepAliveKeepAlive.setPriority(Thread.MIN_PRIORITY);
            keepAliveKeepAlive.setName("PlayerKeepAlive-KeepAlive");
            keepAliveKeepAlive.start();
        }
    }

    public void onShutdown(ShutdownEvent event) {
        reset();
    }

    public static Map<Guild, MusicPlayer> getPlayers() {
        return Collections.unmodifiableMap(players);
    }

    public static Map<MusicPlayer, Thread> getKeepAliveMap() {
        return Collections.unmodifiableMap(keepAliveMap);
    }

    public static int queuedSongs() {
        final int[] amount = {0};
        players.forEach((g, p) -> amount[0] += p.getAudioQueue().size());
        return amount[0];
    }

    public static void reset() {
        Map<Guild, MusicPlayer> tmp = new HashMap<>();
        tmp.putAll(players);
        tmp.forEach((g, p) -> reset(g));
        keepAliveMap.forEach((p, t) -> {
            t.interrupt(); //noinspection deprecation
            //noinspection deprecation
            t.stop();
        });
        keepAliveMap.clear();
    }

    public static void reset(Guild guild) {
        MusicPlayer player = getPlayer(guild);
        if (!player.isStopped())
            player.stop();
        if (!player.getAudioQueue().isEmpty())
            player.getAudioQueue().clear();
        guild.getAudioManager().setSendingHandler(null);
        players.remove(guild);
    }

    public static void setIsLive(MusicPlayer player, boolean isLive) {
        MinnAudioManager.isLive.put(player, isLive);
    }

    public static boolean isLive(MusicPlayer player) {
        if(isLive.containsKey(player)) return isLive.get(player);
        return false;
    }

    private synchronized static void removeWith(BiConsumer<Guild, MusicPlayer> runnable, Map<Guild, MusicPlayer> toRemove) {
        players.forEach(runnable);
        toRemove.forEach((g, p) -> players.remove(g, p));
    }

    public static void clear() {
        Map<Guild, MusicPlayer> toRemove = new HashMap<>();
        removeWith((g, p) -> {
            if (p.getAudioQueue().isEmpty() && !p.isPlaying()) {
                if (g.getAudioManager().getConnectedChannel() != null)
                    g.getAudioManager().closeAudioConnection();
                toRemove.put(g, p);
            }
        }, toRemove);

    }

    public static MusicPlayer getPlayer(Guild guild) {
        MusicPlayer player = players.get(guild);
        return player != null ? player : registerPlayer(new MusicPlayer(), guild);
    }

    private static MusicPlayer registerPlayer(MusicPlayer player, Guild guild) {
        if (player == null) {
            throw new UnsupportedOperationException("Player can not be null!");
        }
        guild.getAudioManager().setSendingHandler(player);
        players.put(guild, player);
        Thread keepAlive = new PlayerKA(player, guild, players);
        keepAliveMap.put(player, keepAlive);
        player.setVolume(.5f);
        // player.addEventListener(new PlayerListener());
        return player;
    }

    private static class PlayerKA extends Thread {

        private Guild guild;
        private MusicPlayer player;
        private Map<Guild, MusicPlayer> players;

        PlayerKA(MusicPlayer player, Guild guild, Map<Guild, MusicPlayer> players) {
            this.guild = guild;
            this.player = player;
            this.players = players;
            setDaemon(true);
            setName("Player-KeepAlive(" + guild.getName() + ")");
            start();
        }

        public void run() {
            while (!this.isInterrupted()) {
                try {
                    Thread.sleep(TimeUnit.MINUTES.toMillis(10L));
                } catch (InterruptedException ignored) {
                    break;
                }
                if ((player.getAudioQueue().isEmpty() && !player.isPlaying()) || !guild.getAudioManager().isConnected()) { // Player queue empty or not connected? If yes -> kill
                    break;
                }
            }
            if (guild.getAudioManager().getConnectedChannel() != null)
                guild.getAudioManager().closeAudioConnection();
            guild.getAudioManager().setSendingHandler(null);
            players.remove(guild, player);
            this.interrupt();
        }
    }
}
