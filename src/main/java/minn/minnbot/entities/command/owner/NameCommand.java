package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class NameCommand extends ListenerAdapter implements Command {

	private User owner;
	private Logger logger;
	private String prefix;

	public NameCommand(User owner, String prefix, Logger logger) {
		this.owner = owner;
		this.logger = logger;
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
			event.event.getJDA().getAccountManager().setUsername(event.allArguments);
			event.sendMessage("Requested name change.");
		} catch (Exception e) {
			event.sendMessage("Encountered Exception: " + e.getMessage());
		}
	}

	@Override
	public boolean isCommand(String message) {
		message = message.toLowerCase();
		if (!message.startsWith(prefix)) {
			return false;
		}
		String command = message.substring(prefix.length());
		return ((command.startsWith("name ") && command.length() > "name ".length()) || command.equals("name"));
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
}
