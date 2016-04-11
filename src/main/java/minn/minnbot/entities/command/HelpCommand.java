package minn.minnbot.entities.command;

import java.util.List;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class HelpCommand extends ListenerAdapter implements Command {

	private final String prefix;
	private Logger logger;
	private List<Command> commands;

	public HelpCommand(String prefix, Logger logger, List<Command> commands) {
		this.prefix = prefix;
		this.logger = logger;
		this.commands = commands;
	}

	public void onMessageReceived(MessageReceivedEvent event) {
		String message = event.getMessage().getContent();
		if (isCommand(message)) {
			logger.logCommandUse(event.getMessage());
			onCommand(new CommandEvent(event));
		}
	}

	@Override
	public void setLogger(Logger logger) {
		if (logger == null)
			throw new IllegalArgumentException("Logger cannot be null");
		this.logger = logger;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public void onCommand(CommandEvent event) {
		if (event.allArguments.isEmpty()) {
			String rngAlias = commands.get(((int) Math.floor((Math.random() * commands.size())))).getAlias();
			String s = "**__Example: " + prefix + rngAlias + "__**```xml";
			for (Command c : commands) {
				if (!(c instanceof HelpCommand)) {
					String alias = c.getAlias().replace("`", "");
					s += "\n" + ((c.requiresOwner()) ? "[OP] " : "") + alias;
				}
			}
			event.sendMessage(s + "```");
			return;
		}
		String cmd = event.allArguments.split(" ",2)[0];
		if(!event.allArguments.startsWith(prefix))
			cmd = prefix + cmd;
		for (Command c : commands) {
			if (c.isCommand(cmd)) {
				if (!c.usage().isEmpty())
					event.sendMessage("Usage page for " + cmd + ": " + c.usage());
				else
					event.sendMessage("No usage page given.");
				return;
			}
		}
		event.sendMessage("Unrecognised command `" + event.allArguments + "`\nUsage: " + usage());
	}

	@Override
	public boolean isCommand(String message) {
		try { 
			message = message.toLowerCase();
			if (!message.startsWith(prefix))
				return false;
			message = message.substring(prefix.length());
			String command = message.split(" ", 2)[0];
			if (command.equalsIgnoreCase("help"))
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public String usage() {
		return "```xml\n" + prefix + "help <command>```";
	}

	@Override
	public String getAlias() {
		return "help <command>";
	}

	@Override
	public boolean requiresOwner() {
		return false;
	}

}
