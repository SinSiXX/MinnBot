package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.entities.impl.LoggerImpl;
import minn.minnbot.events.CommandEvent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DebugCommand extends CommandAdapter {

	public DebugCommand(String prefix, Logger logger) {
		init(prefix, logger);
	}

	@Override
	public void onCommand(CommandEvent event) {
		try {
			if(event.allArguments.isEmpty()) {
				event.sendMessage(String.format("**Invalid debug level! Levels:** `%s`\n**__Current:__** `%s`", Arrays.toString(LoggerImpl.DebugLevel.values()), logger.toggleDebug(null).name()));
				return;
			}
			List<LoggerImpl.DebugLevel> levelList = Arrays.asList(LoggerImpl.DebugLevel.values()).stream().filter(l -> l.name().equalsIgnoreCase(event.allArguments)).collect(Collectors.toList());
			if(levelList.isEmpty()) {
				event.sendMessage(String.format("**Invalid debug level! Levels:** `%s`", Arrays.toString(LoggerImpl.DebugLevel.values())));
				return;
			}
			event.sendMessage("**__Debug:__** " + logger.toggleDebug(levelList.get(0)));
		} catch (Exception e) {
			event.sendMessage("Encountered Exception: " + e.getMessage());
		}
	}

	@Override
	public String getAlias() {
		return "td <level>";
	}

	public boolean requiresOwner() {
		return true;
	}

	@Override
	public String example() {
		return "td FILE";
	}

	public String usage() {
		return "Sets the logger level for the event flow! Levels: LOG, FILE, NONE";
	}

}
