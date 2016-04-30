package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

public class SayCommand extends CommandAdapter {

	public SayCommand(String prefix, Logger logger) {
		init(prefix, logger);
	}

	@Override
	public void onCommand(CommandEvent event) {
		event.sendMessage("\u0001" + event.allArguments);
	}

	@Override
	public boolean isCommand(String message) {
		String[] parts = message.split(" ", 2);
		return parts.length > 0 && parts[0].equalsIgnoreCase(prefix + "say");
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
