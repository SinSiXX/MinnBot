package minn.minnbot.entities.impl;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.LogWriter;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.PublicLog;
import minn.minnbot.entities.throwable.Info;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.gui.MinnBotUserInterface;
import minn.minnbot.util.TimeUtil;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.DisconnectEvent;
import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.ShutdownEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.utils.SimpleLog;

import java.io.*;
import java.rmi.UnexpectedException;
import java.util.*;

public class LoggerImpl extends ListenerAdapter implements Logger, Thread.UncaughtExceptionHandler, SimpleLog.LogListener {

    private int messages = 0;
    private int privateMessages = 0;
    private int guildMessages = 0;
    private int commands = 0;
    private int events = 0;
    private boolean debug = false;
    // private List<String> messageLogs;
    private List<String> errorLogs;
    private MinnBotUserInterface console;
    private long startTime;
    private boolean logMessages = false;
    private boolean logEvents = false;
    public static boolean log = true;
    private static Map<Command, Integer> commandUse = new HashMap<>();
    private LogWriter errorLogWriter;
    private LogWriter messageLogWriter;
    private PublicLog publicLog;

    public LoggerImpl(MinnBotUserInterface console) {
        this.console = console;
        // messageLogs = new LinkedList<String>();
        errorLogs = new LinkedList<>();
        this.startTime = System.currentTimeMillis();
        console.writeEvent(TimeUtil.timeStamp() + "[MINNBOT] Ready!");
        console.writeln(TimeUtil.timeStamp() + "[MINNBOT] Ready!");
        SimpleLog.addListener(this);
        try {
            this.errorLogWriter = new LogWriter();
            File folder = new File("Logs/Session/Messages");
            folder.mkdirs();
            this.messageLogWriter = new LogWriter(new File(String.format("Logs/Session/Messages/Session-%s.log", 10 << System.currentTimeMillis())));
        } catch (IOException e) {
            logThrowable(e);
        }
    }

    @Override
    public void onEvent(Event event) {
        if (publicLog == null)
            publicLog = PublicLog.init(event.getJDA());
        if (event instanceof MessageReceivedEvent) {
            onMessageReceived((MessageReceivedEvent) event);
        } else if (event instanceof ShutdownEvent || event instanceof DisconnectEvent) {
            saveToJson("ErrorLog-Session_" + (11 << System.currentTimeMillis()) + ".log", errorLogs);
            if (event instanceof ShutdownEvent)
                try {
                    errorLogWriter.close();
                    messageLogWriter.close();
                } catch (IOException e) {
                    logThrowable(e);
                }
        }
        logEvent(event);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event == null) {
            logThrowable(new UnexpectedException("MessageReceivedEvent was null."));
            return;
        }
        if (!event.isPrivate()) {
            guildMessages++;
            messages++;
        } else {
            privateMessages++;
            messages++;
        }
        if (!event.getAuthor().isBot())
            logMessage(event.getMessage());
        // if(messages > 2000)
        // saveToJson("message", messageLogs);
    }

    @Override
    public boolean logMessage(Message m) {
        if (!logMessages)
            return false;
        try {
            String s = String.format("%s [%s] %s", TimeUtil.timeStamp(), String.format("%s#%s", m.getAuthor().getUsername(), m.getAuthor().getDiscriminator()), m.getContent());
            messageLogWriter.writeLn(s);
            console.writeln(s);
            // messageLogs.add(s);
            return true;
        } catch (Exception e) {
            logThrowable(e);
            return false;
        }
    }

    @Override
    public boolean logCommandUse(Message m, Command c, CommandEvent event) {
        PublicLog.log(String.format("**%s %s#%s used `%s` in #%s**", TimeUtil.timeStamp(), m.getAuthor().getUsername(), m.getAuthor().getDiscriminator(), c.getAlias().split("\\s+", 2)[0],
                (m.isPrivate()
                        ? m.getAuthor().getUsername()
                        : String.format("%s of %s", ((TextChannel) m.getChannel()).getName(), ((TextChannel) m.getChannel()).getGuild().getName()))), event.author);
        if (commandUse.containsKey(c)) commandUse.put(c, commandUse.get(c) + 1);
        else commandUse.put(c, 1);
        commands++;
        return true;
    }

    @Override
    public String mostUsedCommand() {
        final Command[] com = {null};
        final int[] i = {0};
        commandUse.forEach((c, n) -> {
            if (n > i[0]) {
                com[0] = c;
                i[0] = n;
            }
        });
        if (com != null)
            return String.format("[%s](%s)", com[0].getAlias().split("\\s+", 2)[0], i[0]);
        else
            return "none";
    }

    @Override
    public boolean logEvent(Event event) {
        try {
            if (debug) {
                String s = TimeUtil.timeStamp() + " " + event.getClass().getSimpleName() + ": "
                        + event.getJDA();
                console.writeEvent(s);
                events++;
                return true;
            } else
                return false;
        } catch (Exception e) {
            logThrowable(e);
            return false;
        }
    }

    private void write(String input) {
        errorLogWriter.writeLn(input);
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
     * <b>3)</b> privateMessages
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

    public boolean saveToJson() {
        return saveToJson("ErrorLog-Session_" + (11 << System.currentTimeMillis()) + ".log", errorLogs);
    }

    @SuppressWarnings({"finally", "ThrowFromFinallyBlock", "ReturnInsideFinallyBlock"})
    public boolean saveToJson(String name, List<String> list) {
        if (list.isEmpty() || name.isEmpty())
            return false;
        File f = new File("Logs/" + name);
        if (f.exists())
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        String[] s = {"<-!-Error-Logs-->\n"};
        list.parallelStream().forEachOrdered(element -> s[0] += element + "\n");
        Writer out = null;
        //noinspection ResultOfMethodCallIgnored
        new File("Logs").mkdirs();
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("Logs/" + f.getName()), "UTF-8"));
            try {
                out.write(s[0]);
            } finally {
                out.close();
                list.clear();
                return true;
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    @Override
    public boolean logThrowable(Throwable e) {
        if (!logEvents)
            return false;
        final String[] s = new String[1];
        if (e instanceof Info)
            s[0] = TimeUtil.timeStamp() + " " + e.getMessage();
        else
            s[0] = TimeUtil.timeStamp() + " " + e.getClass().getSimpleName() + ": " + e.getMessage();
        if (e instanceof Info) {
            console.writeEvent("[Info] " + s[0]);
            write("[Error] " + s[0]);
        } else {
            final int[] elements = {0};
            Arrays.stream(e.getStackTrace()).forEachOrdered((element) -> {
                if (elements[0] < 5) {
                    s[0] += "\n\t" + element.toString();
                    elements[0]++;
                }
            });
            errorLogs.add(s[0]);
            String string = "[Error] " + s[0];
            console.writeEvent(string);
            write(string);
            PublicLog.log(String.format("```%s```", string));
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void toggleErrorLog() {
        logEvents = !logEvents;
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
        logThrowable(e);
    }

    @Override
    public void onLog(SimpleLog log, SimpleLog.Level level, Object msg) {
        if (level.getPriority() < SimpleLog.Level.INFO.getPriority()) //lower than info
            return;
        logThrowable(new Info('[' + log.name + "] " + msg.toString()));
    }

    @Override
    public void onError(SimpleLog simpleLog, Throwable throwable) {
        logThrowable(throwable);
    }
}
