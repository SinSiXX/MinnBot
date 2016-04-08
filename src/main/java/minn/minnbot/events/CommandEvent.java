package minn.minnbot.events;

import minn.minnbot.util.TimeUtil;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class CommandEvent {

	public final String allArguments;
	public final String[] arguments;
	public final MessageReceivedEvent event;
	public final boolean isPrivate;
	public final String timeStamp;

	public CommandEvent(MessageReceivedEvent event) {
		arguments = event.getMessage().getContent().split(" ");
		String checked;
		try {
			checked = event.getMessage().getContent().split(" ", 2)[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			checked = "";
		}
		allArguments = checked;
		this.event = event;
		isPrivate = event.isPrivate();
		timeStamp = TimeUtil.timeStamp();
	}

	public void sendMessage(String content) {
		if (event.getTextChannel().checkPermission(event.getJDA().getSelfInfo(), Permission.MESSAGE_WRITE))
			event.getChannel().sendMessageAsync(content, null);
	}

	public Message sendMessageBlocking(String content) {
		if (event.getTextChannel().checkPermission(event.getJDA().getSelfInfo(), Permission.MESSAGE_WRITE))
			return event.getChannel().sendMessage(content);
		return null;
	}
	
}
