package minn.minnbot.entities;

import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public interface Command {
	
	void onMessageReceived(MessageReceivedEvent event);
	
	void setLogger(Logger logger);
	
	Logger getLogger();
	
	void onCommand(CommandEvent event);
	
	boolean isCommand(String message);
	
	String usage();
	
	String getAlias();
	
	boolean requiresOwner();
}
