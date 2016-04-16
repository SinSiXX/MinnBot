package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class ShutdownCommand extends CommandAdapter {

	private User owner;

	public ShutdownCommand(String prefix, User owner, Logger logger) {
		this.logger = logger;
		this.owner = owner;
		this.prefix = prefix;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor() == owner && isCommand(event.getMessage().getContent())) {
			logger.logCommandUse(event.getMessage());
			onCommand(new CommandEvent(event));
		}
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
	public boolean isCommand(String message) {
		message = message.toLowerCase();
		return message.equals(prefix + "shutdown");
	}

	@Override
	public String getAlias() {
		return "shutdown";
	}

	public boolean requiresOwner() {
		return true;
	}
}
