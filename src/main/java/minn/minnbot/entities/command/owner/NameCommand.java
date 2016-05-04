package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

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
	public boolean isCommand(String message) {
		String[] p = message.split(" ",2);
		return p.length > 0 && p[0].equalsIgnoreCase(prefix + "name");
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
