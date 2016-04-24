package minn.minnbot.manager;

import minn.minnbot.entities.audio.PlayerListener;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.player.MusicPlayer;

import java.util.HashMap;
import java.util.Map;

public class MinnAudioManager {

    private static Map<Guild, MusicPlayer> players = new HashMap<>();

    public static void reset() {
        players.forEach(((guild, player) -> {
            if (!player.isStopped())
                player.stop();
            players.remove(guild);
        }));
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
        player.addEventListener(new PlayerListener());
        return player;
    }

}
