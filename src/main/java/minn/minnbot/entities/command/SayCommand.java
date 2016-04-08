package minn.minnbot.entities.command;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class SayCommand extends ListenerAdapter implements Command {

	private String prefix;
	private Logger logger;

	public SayCommand(String prefix, Logger logger) {
		this.prefix = prefix;
		this.logger = logger;
	}
	
	public void onMessageReceived(MessageReceivedEvent event) {
		if(isCommand(event.getMessage().getContent())) {
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
		event.sendMessage("\u0001" + event.allArguments);
	}

	@Override
	public boolean isCommand(String message) {
		String[] parts = message.split(" ", 2);
		if (parts.length != 2)
			return false;
		return parts[0].equalsIgnoreCase(prefix + "say");
	}

	@Override
	public String usage() {
		return "";
	}

	@Override
	public String getAlias() {
		return "`say <arguments>`";
	}

	@Override
	public boolean requiresOwner() {
		return false;
	}

}
