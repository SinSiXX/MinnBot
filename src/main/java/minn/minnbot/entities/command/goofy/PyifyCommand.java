package minn.minnbot.entities.command.goofy;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

import java.util.List;

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
    public boolean isCommand(String message, List<String> prefixList) {
        String[] p = message.split(" ", 2);
        if(p.length < 1)
            return false;
        if(p[0].equalsIgnoreCase(prefix + "pyify"))
            return true;
        for(String fix : prefixList) {
            if(p[0].equalsIgnoreCase(fix + "pyify"))
                return true;
        }
        return false;
    }

    @Override
    public String getAlias() {
        return "pyify <text>";
    }

    public String example() {
        return "pyify spoopy";
    }

}
