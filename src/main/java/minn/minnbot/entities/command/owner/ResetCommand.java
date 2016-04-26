package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

public class ResetCommand extends CommandAdapter {

   public ResetCommand(String prefix, Logger logger) {
       init(prefix,logger);
   }

    @Override
    public void onCommand(CommandEvent event) {

    }

    @Override
    public boolean isCommand(String message) {
        String[] p = message.split(" ",2);
        return p.length > 0 && p[0].equalsIgnoreCase(prefix + "reset");
    }

    public String usage() {
        return "Resets player system. Which means it **clears all queues**, **stops all players** and **removes them from the system**.\n" +
                "However it does __not__ affect the audio connections!";
    }

    @Override
    public String getAlias() {
        return "reset";
    }
}
