package minn.minnbot.entities;

import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.Event;

public interface Logger {

    void toggleErrorLog();

    void toggleMessageLog();

    boolean logMessage(Message m);

    boolean logCommandUse(Message m, Command c, CommandEvent event);

    boolean logEvent(Event e);

    boolean logThrowable(Throwable e);

    boolean toggleDebug();

    /**
     * Array with stat numbers: <b>0)</b> messages
     * <b>1)</b> commands
     * <b>2)</b> events
     * <b>3(</b> privateMessages
     * <b>4)</b> guildMessages
     * <b>5)</b> startTimeInMillis
     */
    int[] getNumbers();

    boolean saveToJson();

    String mostUsedCommand();

}
