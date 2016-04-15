package minn.minnbot.entities.impl;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.throwable.Info;
import minn.minnbot.gui.MinnBotUserInterface;
import minn.minnbot.util.TimeUtil;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

import java.util.List;

public class LoggerImpl extends ListenerAdapter implements Logger, Thread.UncaughtExceptionHandler {

    private int messages = 0;
    private int privateMessages = 0;
    private int guildMessages = 0;
    private int commands = 0;
    private int events = 0;
    private boolean debug = false;
    // private List<String> messageLogs;
    // private List<String> errorLogs;
    private MinnBotUserInterface console;
    private long startTime;
    private boolean logMessages = false;
    private boolean logErrors = false;

    public LoggerImpl(MinnBotUserInterface console) {
        this.console = console;
        // messageLogs = new LinkedList<String>();
        // errorLogs = new LinkedList<String>();
        this.startTime = System.currentTimeMillis();
        console.writeEvent(TimeUtil.timeStamp() + "[MINNBOT] Ready!");
        console.writeln(TimeUtil.timeStamp() + "[MINNBOT] Ready!");
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof MessageReceivedEvent)) {
            logEvent(event);
        } else {
            onMessageReceived((MessageReceivedEvent) event);
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        messages++;
        if (event.isPrivate())
            privateMessages++;
        else
            guildMessages++;
        logMessage(event.getMessage());

        // if(messages > 2000)
        // saveToJson("message", messageLogs);
        logEvent(event);
    }

    @Override
    public boolean logMessage(Message m) {
        if (!logMessages)
            return false;
        try {
            String stamp = TimeUtil.timeStamp();
            String s = stamp + "[" + m.getAuthor() + "] " + m.getContent();
            console.writeln(s);
            // messageLogs.add(s);
            messages++;
            return true;
        } catch (Exception e) {
            logError(e);
            return false;
        }
    }

    @Override
    public boolean logCommandUse(Message m) {
        commands++;
        return true;
    }

    @Override
    public boolean logEvent(Event event) {
        try {
            if (debug) {
                String s = TimeUtil.timeStamp() + " " + event.getClass().getSimpleName() + ": "
                        + event.getJDA();
                console.writeEvent(s);
                // errorLogs.add(s);
                events++;
                return true;
            } else
                return false;
        } catch (Exception e) {
            logError(e);
            return false;
        }
    }

    @Override
    public boolean toggleDebug() {
        debug = !debug;
        return debug;
    }

    /**
     * Array with stat numbers: <b>0)</b> messages
     * <b>1)</b> commands
     * <b>2)</b> events
     * <b>3(</b> privateMessages
     * <b>4)</b> guildMessages
     * <b>5)</b> startTimeInMillis
     */
    @Override
    public int[] getNumbers() {
        int[] numbers = new int[6];
        numbers[0] = messages;
        numbers[1] = commands;
        numbers[2] = events;
        numbers[3] = privateMessages;
        numbers[4] = guildMessages;
        numbers[5] = (int) (System.currentTimeMillis() - startTime);
        return numbers;
    }

    public boolean saveToJson(String name, List<String> list) {
        // TODO
        return false;
    }

    @Override
    public boolean logError(Throwable e) {
        if (!logErrors && !(e instanceof Info))
            return false;
        String s;
        if (e instanceof Info)
            s = TimeUtil.timeStamp() + " " + e.getMessage();
        else
            s = TimeUtil.timeStamp() + " " + e.getClass().getSimpleName() + ": " + e.getMessage();
        if (e instanceof Info) {
            console.writeEvent("[Info] " + s);
        } else {
            console.writeEvent("[Error] " + s);
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void toggleErrorLog() {
        logErrors = !logErrors;
    }

    @Override
    public void toggleMessageLog() {
        logMessages = !logMessages;
    }

    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     *
     * @param t the thread
     * @param e the exception
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logError(e);
    }
}
