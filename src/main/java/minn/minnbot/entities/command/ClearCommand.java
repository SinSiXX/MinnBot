package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.MessageHistory;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class ClearCommand extends CommandAdapter {

	public ClearCommand(String prefix, Logger logger) {
		this.logger = logger;
		this.prefix = prefix;
	}

	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.isPrivate())
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
	@SuppressWarnings("deprecation")
	public void onCommand(CommandEvent event) {
		try {
			Thread t = new Thread(() -> {
					int amount = 100;
				final int[] count = {0};
					try {
						amount = Integer.parseInt(event.allArguments);
					} catch (NumberFormatException ignored) {
					}
					java.util.List<Message> hist = new MessageHistory(event.event.getTextChannel()).retrieve(amount);
					hist.parallelStream().forEach(m -> {
						Thread t2 = new Thread(() -> {
								m.deleteMessage();
								Thread.currentThread().stop();
							});
						t2.start();
						count[0]++;
					});
					event.sendMessage(event.event.getAuthor().getAsMention() + ", deleted " + count[0] + " messages in this channel.");
					Thread.currentThread().stop();
				});
			t.start();
		} catch (Exception e) {
			logger.logThrowable(e);
		}
	}

	@Override
	public boolean isCommand(String message) {
		String[] parts = message.split(" ", 2);
		if (parts.length < 1)
			return false;
		return parts[0].equalsIgnoreCase(prefix + "clear");
	}

	@Override
	public String usage() {
		return "`clear <amount>` or `clear`\t | Required Permissions: Manage Messages";
	}

	@Override
	public String getAlias() {
		return "clear <amount>";
	}

	@Override
	public String example() {
		return "clear 50";
	}

}
