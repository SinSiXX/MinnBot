package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class SayCommand extends CommandAdapter {

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
	public String getAlias() {
		return "`say <arguments>`";
	}

	@Override
	public String example() {
		return "say SOMETHING LEWD!!!";
	}

}
