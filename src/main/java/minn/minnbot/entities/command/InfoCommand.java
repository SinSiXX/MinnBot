package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.User;

public class InfoCommand extends CommandAdapter {

	private String ownername;
	private String inviteUrl;
	private final boolean bot;

	public InfoCommand(String prefix, Logger logger, User owner, String inviteUrl, boolean bot) {
		this.prefix = prefix;
		this.logger = logger;
		if (owner != null)
			this.ownername = owner.getUsername();
		else
			this.ownername = "unknown";
		if (inviteUrl != null)
			this.inviteUrl = inviteUrl;
		else
			this.inviteUrl = "";
		this.bot = bot; // Possible client support
	}

	@Override
	public void onCommand(CommandEvent event) {
		String s = "**__Info page:__**\n\n";
		s += "I'm the second incarnation of MinnBot:tm:\n";
		s += "Coded in JDA(" + net.dv8tion.jda.JDAInfo.VERSION
				+ ") by my creator Minn, I was selected to work on behalf of **" + ownername + "**.\n";
		s += "You can view my commands by typing **" + prefix + "help** in the chat.\n";
		s += "If you want to see my **source code**,"
				+ " here is a link to my github page: **<http://minndevelopment.github.io/MinnBot>**\n";
		s += "Visit the official development server for further information: **<https://discord.gg/0mcttggeFpcMByUz>**\n";
		if (!inviteUrl.isEmpty() && bot)
			s += "Make me join your server with this url:\n**<" + inviteUrl + ">**";
		event.sendMessage(s);
	}

	@Override
	public boolean isCommand(String message) {
		try {
			message = message.toLowerCase();
			if (!message.startsWith(prefix))
				return false;
			message = message.substring(prefix.length());
			String command = message.split(" ", 2)[0];
			if (command.equalsIgnoreCase("info"))
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public String getAlias() {
		return "info";
	}

	@Override
	public String example() {
		return "info";
	}

}
