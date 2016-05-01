package minn.minnbot.entities.command.custom;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomRegister {

    private static final List<Command> CUSTOMS = new LinkedList<>();
    private static final ExecutorService basicExecutor = new ThreadPoolExecutor(1,1,1L, TimeUnit.MINUTES,new LinkedBlockingDeque<>(), r -> {
        final Thread thread = new Thread(r, "TestThread");
        thread.setDaemon(true);
        thread.setPriority(1);
        return thread;
    });

    public static List<Command> getCustoms(String prefix, Logger logger) { // REGISTER CUSTOM COMMANDS
        // Example
        /*CUSTOMS.add(new AdvancedCCommand(prefix, logger)
                .setHelpAlias("testcommand").setInternalAlias("testcommand")
                .setPrivateAllowed(true)
                .setUsage("This is to test advanced commands.")
                .setConsume((event) -> event.sendMessage("This is a test response."))
                .setExecutor(basicExecutor));*/



        return CUSTOMS;
    }

}
