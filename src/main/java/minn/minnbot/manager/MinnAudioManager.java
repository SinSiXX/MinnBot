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

    /*private static Thread keepAlive;*/

    private static Map<Guild, MusicPlayer> players = new HashMap<>();

    private static Map<MusicPlayer, Thread> keepAliveMap = new HashMap<>();

    public MinnAudioManager() {
        init();
    }

    public static void init() {
        /*if (keepAlive == null) {
            keepAlive = new Thread(() -> {
                while (!keepAlive.isInterrupted()) {
                    if (!players.isEmpty()) {
                        clear();
                    }
                    try {
                        Thread.sleep(60000 * 5);
                    } catch (InterruptedException ignored) {
                    }
                }
            });
            keepAlive.setDaemon(true);
            keepAlive.setName("MinnAudioManager-KeepAlive");
            keepAlive.start();
        }*/
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
        players.forEach((g, p) -> {
            if (!p.isStopped())
                p.stop();
            p.getAudioQueue().clear();
        });
        players.clear();
        keepAliveMap.forEach((p, t) -> t.interrupt());
        keepAliveMap.clear();
    }

    public static void reset(Guild guild) {
        players.remove(guild);
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

    public static MusicPlayer registerPlayer(MusicPlayer player, Guild guild) {
        if (player == null) {
            throw new UnsupportedOperationException("Player can not be null!");
        }
        guild.getAudioManager().setSendingHandler(player);
        players.put(guild, player);
        Thread keepAlive = new Thread(() -> {
            while (!keepAliveMap.get(player).isInterrupted()) {
                try {
                    Thread.sleep(TimeUnit.MINUTES.toMillis(10L));
                } catch (InterruptedException ignored) {
                    if (guild.getAudioManager().getConnectedChannel() != null)
                        guild.getAudioManager().closeAudioConnection();
                    break;
                }
                if (player.getAudioQueue().isEmpty() && !player.isPlaying()) {
                    if (guild.getAudioManager().getConnectedChannel() != null)
                        guild.getAudioManager().closeAudioConnection();
                    break;
                }
            }
            players.remove(guild, player);
        });
        keepAlive.setName("Player-KeepAlive(" + guild.getName() + ")");
        keepAliveMap.put(player, keepAlive);
        keepAlive.start();
        player.setVolume(.5f);
        // player.addEventListener(new PlayerListener());
        return player;
    }

}
