package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class DebugCommand extends ListenerAdapter implements Command {

	private User owner;
	private String prefix;
	private Logger logger;
	
	public DebugCommand(User owner, String prefix, Logger logger) {
		this.owner = owner;
		this.prefix = prefix;
		this.logger = logger;		
	}

	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.getAuthor() == owner && isCommand(event.getMessage().getContent())) {
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
			event.sendMessage("**__Debug:__** " + logger.toggleDebug());
		} catch (Exception e) {
			event.sendMessage("Encountered Exception: " + e.getMessage());
		}
	}

	@Override
	public boolean isCommand(String message) {
		message = message.toLowerCase();
		return message.equals(prefix + "toggledebug") || message.equals(prefix + "td");
	}

	@Override
	public String usage() {
		return "`toggledebug` or `td`";
	}

	@Override
	public String getAlias() {
		return "toggledebug";
	}

	public boolean requiresOwner() {
		return true;
	}
	
}
