package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.PlayingFieldManager;

public class GameCommand extends CommandAdapter {

    public GameCommand(String prefix, Logger logger) {
        super.init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        if(event.allArguments.isEmpty() || event.allArguments.length() > 20) {
            event.sendMessage("Game name must be between 0-20 chars.");
            return;
        }
        PlayingFieldManager.addGame(event.allArguments);
        event.sendMessage("Added game to list!");
    }

    @Override
    public boolean isCommand(String message) {
        String[] p = message.split(" ", 2);
        return p.length > 0 && p[0].equalsIgnoreCase(prefix + "game");
    }

    @Override
    public String usage() {
        return "`game <game>`";
    }

    @Override
    public String getAlias() {
        return "game <game>";
    }

    public boolean requiresOwner() {
        return true;
    }

    @Override
    public String example() {
        return "game boobs";
    }

}
