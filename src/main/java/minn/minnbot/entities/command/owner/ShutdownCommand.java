package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

public class ShutdownCommand extends CommandAdapter {

	public ShutdownCommand(String prefix, Logger logger) {
		this.logger = logger;
		this.prefix = prefix;
	}

	@Override
	public void onCommand(CommandEvent event) {
		try {
			event.sendMessageBlocking("Shutting down...");
			event.event.getJDA().shutdown();
		} catch (Exception e) {
			event.sendMessage("Encountered Exception: " + e.getMessage());
		}
	}

	@Override
	public String getAlias() {
		return "shutdown";
	}

	public boolean requiresOwner() {
		return true;
	}

	@Override
	public String example() {
		return "shutdown";
	}

}
