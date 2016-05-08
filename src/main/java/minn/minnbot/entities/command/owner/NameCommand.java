package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

import java.util.List;

public class NameCommand extends CommandAdapter {

	public NameCommand(String prefix, Logger logger) {
		this.logger = logger;
		this.prefix = prefix;
	}

	@Override
	public void onCommand(CommandEvent event) {
		try {
			event.event.getJDA().getAccountManager().setUsername(event.allArguments);
			event.sendMessage("Requested name change.");
		} catch (Exception e) {
			event.sendMessage("Encountered Exception: " + e.getMessage());
		}
	}

	@Override
	public boolean isCommand(String message, List<String> prefixList) {
		String[] p = message.split(" ", 2);
		if(p.length < 1)
			return false;
		if(p[0].equalsIgnoreCase(prefix + "name"))
			return true;
		for(String fix : prefixList) {
			if(p[0].equalsIgnoreCase(fix + "name"))
				return true;
		}
		return false;
	}

	@Override
	public String usage() {
		return "`name <name>`";
	}

	@Override
	public String getAlias() {
		return "name <name>";
	}
	public boolean requiresOwner() {
		return true;
	}

	@Override
	public String example() {
		return "name MinnBot";
	}
}
