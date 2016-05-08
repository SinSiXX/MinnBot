package minn.minnbot.entities.command.moderation;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.manager.CommandManager;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.List;

public class UnsilenceCommand extends CommandAdapter {

	public UnsilenceCommand(String prefix, Logger logger) {
		this.prefix = prefix;
		this.logger = logger;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.isPrivate())
			return;
		String message = event.getMessage().getContent();
		if (isCommand(message, CommandManager.getPrefixList(event.getGuild().getId()))) {
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
		if(mentions.isEmpty()) {
			event.sendMessage("Usage: " + usage());
			return;
		}
		User u = mentions.get(0);
		event.event.getTextChannel().createPermissionOverride(u).grant(Permission.MESSAGE_WRITE).update();
		event.sendMessage(u.getAsMention() + " has been un-silenced.");
	}

	@Override
	public boolean isCommand(String message, List<String> prefixList) {
		String[] p = message.split(" ", 2);
		if(p.length < 1)
			return false;
		if(p[0].equalsIgnoreCase(prefix + "unsilence"))
			return true;
		for(String fix : prefixList) {
			if(p[0].equalsIgnoreCase(fix + "unsilence"))
				return true;
		}
		return false;
	}

	@Override
	public String usage() {
		return "`unsilence @username`\t | Required Permissions: Manage Permissions";
	}

	@Override
	public String getAlias() {
		return "unsilence <mention>";
	}

	@Override
	public String example() {
		return "unsilence <@158174948488118272>";
	}

}
