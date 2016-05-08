package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

import java.util.List;

public class SayCommand extends CommandAdapter {

	public SayCommand(String prefix, Logger logger) {
		init(prefix, logger);
	}

	@Override
	public void onCommand(CommandEvent event) {
		event.sendMessage("\u0001" + event.allArguments);
	}

	@Override
	public boolean isCommand(String message, List<String> prefixList) {
		String[] p = message.split(" ", 2);
		if(p.length < 1)
			return false;
		if(p[0].equalsIgnoreCase(prefix + "say"))
			return true;
		for(String fix : prefixList) {
			if(p[0].equalsIgnoreCase(fix + "say"))
				return true;
		}
		return false;
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
