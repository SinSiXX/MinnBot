package minn.minnbot.entities.command.goofy;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

public class PyifyCommand extends CommandAdapter {

    public PyifyCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        if (event.allArguments.length() >= 1000) {
            event.sendMessage("Only accepting input with less than 1k chars.");
            return;
        }
        if (!event.allArguments.toLowerCase().contains("py")) {
            event.sendMessage(event.allArguments + ".py");
            return;
        }
        event.sendMessage(event.allArguments.replace("py", ".py").replace("Py", ".Py").replace("PY", ".PY").replace("pY", ".pY"));
    }

    @Override
    public String getAlias() {
        return "pyify <text>";
    }

    public String example() {
        return "pyify spoopy";
    }

}
