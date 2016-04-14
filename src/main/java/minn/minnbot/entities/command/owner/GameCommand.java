package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class GameCommand extends CommandAdapter {

	private final User owner;
	
	public GameCommand(String prefix, Logger logger, User owner) {
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
	public void onCommand(CommandEvent event) {
		try {
			event.event.getJDA().getAccountManager().setGame(event.allArguments);
			event.sendMessage("Requested game change.");
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
		return ((command.startsWith("game ") && command.length() > "game ".length()) || command.equals("game"));
	}

	@Override
	public String usage() {
		return "`game <game>`";
	}

	@Override
	public String getAlias() {
		return "game <game>";
	}
	public boolean requiresOwner() {
		return true;
	}

}
