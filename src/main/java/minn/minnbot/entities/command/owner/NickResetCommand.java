package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.EmoteUtil;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.exceptions.GuildUnavailableException;
import net.dv8tion.jda.exceptions.PermissionException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NickResetCommand extends CommandAdapter {

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.HOURS, new LinkedBlockingDeque<>(), r -> {
        Thread thread = new Thread(r, "ResettingNicknames");
        thread.setDaemon(true);
        thread.setPriority(1);
        return thread;
    });

    public NickResetCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        executor.submit(() -> {
            LocalTime time = LocalTime.now().plusSeconds(Math.round(event.jda.getGuilds().size() * 5));
            event.sendMessage(String.format("Resetting nicknames... Approximate time of finish: **%s**",
                    time.format(DateTimeFormatter.ofPattern("hh:mm:ss a"))),
                    (m) -> {
                        for(Guild g : event.jda.getGuilds()) {
                            try {
                                event.jda.getAccountManager().setNickname(g, null);
                            } catch (PermissionException | GuildUnavailableException ignored) {
                            }
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ignored) {
                            }
                        }
                        m.updateMessageAsync("Nicknames were reset, if possible. " + EmoteUtil.getRngOkHand(), null);
                    });
        });
    }

    @Override
    public String getAlias() {
        return "nickReset";
    }

    public boolean requiresOwner() {
        return true;
    }

}
