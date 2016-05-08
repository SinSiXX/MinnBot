package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

import java.util.List;

public class DebugCommand extends CommandAdapter {

	public DebugCommand(String prefix, Logger logger) {
		this.prefix = prefix;
		this.logger = logger;		
	}

	@Override
	public void onCommand(CommandEvent event) {
		try {
			event.sendMessage("**__Debug:__** " + logger.toggleDebug());
		} catch (Exception e) {
			event.sendMessage("Encountered Exception: " + e.getMessage());
		}
	}

	@Override
	public boolean isCommand(String message, List<String> prefixList) {
		String[] p = message.split(" ", 2);
		if(p.length < 1)
			return false;
		if(p[0].equalsIgnoreCase(prefix + "td"))
			return true;
		for(String fix : prefixList) {
			if(p[0].equalsIgnoreCase(fix + "td"))
				return true;
		}
		return false;
	}

	@Override
	public String getAlias() {
		return "td";
	}

	public boolean requiresOwner() {
		return true;
	}

	@Override
	public String example() {
		return "td";
	}

}
