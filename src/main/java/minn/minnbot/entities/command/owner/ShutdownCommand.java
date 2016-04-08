package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class ShutdownCommand extends ListenerAdapter implements Command {

	private String prefix;
	private User owner;
	private Logger logger;
	
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
	public void setLogger(Logger logger) {
		if (logger == null)
			throw new IllegalArgumentException("Logger cannot be null.");
		this.logger = logger;
	}

	@Override
	public Logger getLogger() {
		return logger;
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
	public String usage() {
		return "";
	}

	@Override
	public String getAlias() {
		return "shutdown";
	}

	public boolean requiresOwner() {
		return true;
	}
}
