package minn.minnbot.entities;

import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.Event;

public interface Logger {
	
	public void toggleErrorLog();
	
	public void toggleMessageLog();

	public boolean logMessage(Message m);
	
	public boolean logCommandUse(Message m);
	
	public boolean logEvent(Event e);

	public boolean logError(Throwable e);
	
	public boolean toggleDebug();
	
	public int[] getNumbers();
}
