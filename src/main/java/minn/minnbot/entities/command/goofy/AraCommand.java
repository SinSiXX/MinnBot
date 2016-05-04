package minn.minnbot.entities.command.goofy;

import minn.minnbot.entities.AraListener;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.JDA;

public class AraCommand extends CommandAdapter {

    private AraListener listener;

    public AraCommand(String prefix, Logger logger, JDA api) {
        init(prefix, logger);
        Thread t = new Thread(() -> {
                listener = new AraListener(api);
        });
        t.start();
    }

    @Override
    public void onCommand(CommandEvent event) {
        event.sendMessage(listener.getQuote());
    }

    @Override
    public boolean isCommand(String message) {
        String[] p = message.split(" ",2);
        return p.length > 0 && p[0].equalsIgnoreCase(prefix + "ara");
    }

    @Override
    public String getAlias() {
        return "ara";
    }
}
