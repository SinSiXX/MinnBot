package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class LeaveCommand extends ListenerAdapter implements Command {

	private final String prefix;
	private Logger logger;
	private User owner;

	public LeaveCommand(String prefix, Logger logger, User owner) {
		this.prefix = prefix;
		this.logger = logger;
		this.owner = owner;
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
			throw new IllegalArgumentException("Logger can not be null.");
		this.logger = logger;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public void onCommand(CommandEvent event) {
		try {
			Guild g = event.event.getJDA().getGuildById(event.allArguments);
			if (g != null) {
				event.sendMessage("Leaving " + g.getName() + " on your command.");
				g.getManager().leave();
			} else if (event.allArguments.isEmpty()) {
				event.sendMessage("Leaving this guild.");
				event.event.getGuild().getManager().leave();
			} else {
				event.sendMessage("Given id is invalid.");
			}
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
		return ((command.startsWith("leave ") && command.length() > "leave ".length()) || command.equals("leave"));
	}

	@Override
	public String usage() {
		return "`leave <guild-id>`";
	}

	@Override
	public String getAlias() {
		return "leave <guild-id>";
	}

	public boolean requiresOwner() {
		return true;
	}
}
