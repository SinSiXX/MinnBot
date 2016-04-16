package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class DebugCommand extends CommandAdapter {

	private User owner;

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
