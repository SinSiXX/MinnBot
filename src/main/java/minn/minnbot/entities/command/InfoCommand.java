package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

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
		this.bot = bot;
	}

	public void onMessageReceived(MessageReceivedEvent event) {
		if (isCommand(event.getMessage().getContent())) {
			logger.logCommandUse(event.getMessage());
			onCommand(new CommandEvent(event));
		}
	}

	@Override
	public void onCommand(CommandEvent event) {
		String s = "**__Info page:__**\n\n";
		s += "I'm the second incarnation of MinnBot:tm:\n";
		s += "Coded in JDA(" + net.dv8tion.jda.JDAInfo.VERSION
				+ ") by my creator Minn, I was selected to work on behalf of **" + ownername + "**.\n";
		s += "You can view my commands by typing **" + prefix + "help** in the chat.\n";
		s += "If you want to see my **source code**,"
				+ " here is a link to my github page: **<https://github.com/MinnDevelopment/MinnBot.git>**\n";
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
	public String usage() {
		return "";
	}

	@Override
	public String getAlias() {
		return "info";
	}

}
