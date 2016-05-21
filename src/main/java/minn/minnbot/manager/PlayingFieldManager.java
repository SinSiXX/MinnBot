package minn.minnbot.manager;

import minn.minnbot.MinnBot;
import minn.minnbot.entities.Logger;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDAInfo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PlayingFieldManager {

    private static PlayingFieldManager manager;
    private JDA api;
    private Thread managerThread;
    private List<String> games = new LinkedList<>();
    private int index;
    private Logger logger;
    //private boolean iStream;

    public static List<String> getGames() {
        return Collections.unmodifiableList(manager.games);
    }

    private PlayingFieldManager(JDA api, Logger logger) {
        this.api = api;
        this.logger = logger;
        games.add(MinnBot.VERSION);
        games.add("JDA " + JDAInfo.VERSION);
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {
        }
        initThread();

    }

    public static void removeGame(String game) {
        if(manager == null) {
            return;
        }
        if (Objects.equals(game, MinnBot.VERSION)) {
            return;
        }
        if (manager.games.contains(game)) {
            manager.games.remove(game);
        }
    }

    public static void addGame(String game) {
        if(manager == null) {
            return;
        }
        if (manager.games.contains(game)) {
            return;
        }
        manager.games.add(game);
    }

    public static void init(JDA api, Logger logger) {
        if (manager != null) {
            manager.breakThread();
        }
        manager = new PlayingFieldManager(api, logger);
    }

    private void initThread() {
        index = 0;
        managerThread = new Thread(() -> {
            while (index < games.size() && !managerThread.isInterrupted()) {
                try {
                    api.getAccountManager().setGame(games.get(index));
                    Thread.sleep(25000);
                } catch (InterruptedException e) {
                    break;
                } catch (IndexOutOfBoundsException ignored) {
                    api.getAccountManager().setGame(games.get(0));

                    try {
                        Thread.sleep(25000);
                    } catch (InterruptedException ignored2) {
                        break;
                    }
                }
                if (++index >= games.size()) {
                    index = 0;
                }
            }
        });
        managerThread.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) logger);
        managerThread.setName("Playing-Field");
        managerThread.start();
    }

    @SuppressWarnings("deprecated")
    private void breakThread() {
        if (managerThread.isAlive()) {
            //noinspection deprecation
            managerThread.stop();
        }
        managerThread = null;
    }

}
