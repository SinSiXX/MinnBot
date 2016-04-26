package minn.minnbot.manager;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.events.ShutdownEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.player.MusicPlayer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MinnAudioManager extends ListenerAdapter {

    private static Thread keepAlive;

    public MinnAudioManager() {
        init();
    }

    public static void init() {
        if (keepAlive == null) {
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
        }
    }

    public void onShutdown(ShutdownEvent event) {
        // reset();
        if (keepAlive != null && keepAlive.isAlive()) keepAlive.stop();
        keepAlive = null;
        players.clear();
        init();
    }

    private static Map<Guild, MusicPlayer> players = new HashMap<>();

    public static Map<Guild, MusicPlayer> getPlayers() {
        return Collections.unmodifiableMap(players);
    }

    public static int queuedSongs() {
        final int[] amount = {0};
        players.forEach((g, p) -> amount[0] += p.getAudioQueue().size());
        return amount[0];
    }

    public static void reset() {
        Map<Guild, MusicPlayer> toRemove = new HashMap<>();
        players.forEach(((g, p) -> {
            if (!p.isStopped())
                p.stop();
            p.getAudioQueue().clear();
            toRemove.put(g, p);
        }));
        toRemove.forEach((g, p) -> players.remove(g, p));
    }

    public static void clear() {
        Map<Guild, MusicPlayer> toRemove = new HashMap<>();
        players.forEach((g, p) -> {
            if (p.getAudioQueue().isEmpty() && !p.isPlaying()) {
                /*if (g.getAudioManager().getConnectedChannel() != null)
                    g.getAudioManager().closeAudioConnection(); TODO: Decide about use*/
                toRemove.put(g, p);
            }
        });
        toRemove.forEach((g, p) -> players.remove(g, p));
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
        if (!players.containsKey(guild)) {
            players.put(guild, player);
        }
        player.setVolume(.5f);
        // player.addEventListener(new PlayerListener());
        return player;
    }

}
