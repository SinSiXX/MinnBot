package minn.minnbot.entities.command.owner;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.Guild;

import java.util.List;

public class LeaveCommand extends CommandAdapter {

	public LeaveCommand(String prefix, Logger logger) {
		this.prefix = prefix;
		this.logger = logger;
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
	public boolean isCommand(String message, List<String> prefixList) {
		String[] p = message.split(" ", 2);
		if(p.length < 1)
			return false;
		if(p[0].equalsIgnoreCase(prefix + "leave"))
			return true;
		for(String fix : prefixList) {
			if(p[0].equalsIgnoreCase(fix + "leave"))
				return true;
		}
		return false;
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

	@Override
	public String example() {
		return "leave 140412328733704192";
	}

}
