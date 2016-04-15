package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class PurgeCommand extends CommandAdapter {

	public PurgeCommand(String prefix, Logger logger) {
		this.prefix = prefix;
		this.logger = logger;
	}

	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.isPrivate())
			return;
		if (isCommand(event.getMessage().getContent())) {
			if (!event.getTextChannel().checkPermission(event.getAuthor(), Permission.MESSAGE_MANAGE)) {
				return;
			} else if (!event.getTextChannel().checkPermission(event.getJDA().getSelfInfo(),
					Permission.MESSAGE_MANAGE)) {
				event.getChannel()
						.sendMessageAsync("I am unable to delete messages. Missing Permission: MESSAGE_MANAGE", null);
				return;
			}
			logger.logCommandUse(event.getMessage());
			onCommand(new CommandEvent(event));
		}
	}

	@Override
	public void onCommand(CommandEvent event) {
		try {
			User u = event.event.getMessage().getMentionedUsers().get(0);
			Thread t = new Thread() {
				@SuppressWarnings("deprecation")
				public void run() {
					java.util.List<Message> hist = new net.dv8tion.jda.MessageHistory(event.event.getChannel()).retrieve(100);
					for (Message m : hist) {
						if (m.getAuthor() == u) {
							Thread t = new Thread() {
								public void run() {
									m.deleteMessage();
									this.stop();
								}
							};
							t.start();
						}
					}
					event.sendMessage(
							u.getAsMention() + " has been purged by " + event.event.getAuthor().getUsername());
					this.stop();
				}
			};
			t.start();
		} catch (IndexOutOfBoundsException e) {
			event.sendMessage("I am unable to purge without mention reference. Usage: " + usage());
		} catch (Exception e) {
			logger.logThrowable(e);
		}
	}

	@Override
	public boolean isCommand(String message) {
		String[] parts = message.split(" ", 2);
		if (parts.length < 1)
			return false;
		return parts[0].equalsIgnoreCase(prefix + "purge");
	}

	@Override
	public String usage() {
		return "`purge @username`\t | Required Permissions: Manage Messages";
	}

	@Override
	public String getAlias() {
		return "purge <mention>";
	}

}
