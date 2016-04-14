package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.List;

public class SilenceCommand extends CommandAdapter {

	public SilenceCommand(String prefix, Logger logger) {
		this.prefix = prefix;
		this.logger = logger;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.isPrivate())
			return;
		String message = event.getMessage().getContent();
		if (isCommand(message)) {
			if (!event.getTextChannel().checkPermission(event.getAuthor(), Permission.MANAGE_PERMISSIONS)) {
				event.getChannel().sendMessageAsync(
						"You are not able to use that command. Missing permission: `MANAGE_PERMISSIONS`", null);
				return;
			} else if (!event.getTextChannel().checkPermission(event.getJDA().getSelfInfo(),
					Permission.MANAGE_PERMISSIONS)) {
				event.getChannel().sendMessageAsync(
						"I am not able to use that command. Missing permission: `MANAGE_PERMISSIONS`", null);
				return;
			}
			logger.logCommandUse(event.getMessage());
			onCommand(new CommandEvent(event));
		}
	}

	@Override
	public void onCommand(CommandEvent event) {
		List<User> mentions = event.event.getMessage().getMentionedUsers();
		if (mentions.isEmpty()) {
			event.sendMessage("Usage: " + usage());
			return;
		}
		User u = mentions.get(0);
		event.event.getTextChannel().createPermissionOverride(u).deny(Permission.MESSAGE_WRITE).update();
		event.sendMessage(u.getAsMention() + " has been silenced.");
	}

	@Override
	public boolean isCommand(String message) {
		try {
			message = message.toLowerCase();
			if (!message.startsWith(prefix))
				return false;
			message = message.substring(prefix.length());
			String command = message.split(" ", 2)[0];
			if (command.equalsIgnoreCase("silence"))
				return true;
		} catch (Exception e) {
			logger.logError(e);
		}
		return false;
	}

	@Override
	public String usage() {
		return "`silence @username`\t | Required Permissions: Manage Permissions";
	}

	@Override
	public String getAlias() {
		return "silence <mention>";
	}

}
