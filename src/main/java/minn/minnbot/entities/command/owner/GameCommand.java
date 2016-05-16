package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.PlayingFieldManager;

import java.util.List;

public class GameCommand extends CommandAdapter {

    public GameCommand(String prefix, Logger logger) {
        super.init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.allArguments.isEmpty() || event.allArguments.length() > 20) {
            final String[] s = {"Game name must be between 0-20 chars."};
            List<String> games = PlayingFieldManager.getGames();
            if (games.size() > 20) {
                event.sendMessage(s[0]);
                return;
            }
            s[0] += "\n**__Games:__** ";
            games.parallelStream().forEachOrdered(g -> s[0] += "`" + g.replace("`", "") + "`, ");
            event.sendMessage(s[0]);
            return;
        }
        PlayingFieldManager.addGame(event.allArguments);
        event.sendMessage("Added game to list!");
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
