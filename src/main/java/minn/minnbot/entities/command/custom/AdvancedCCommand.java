package minn.minnbot.entities.command.custom;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.concurrent.ExecutorService;

public class AdvancedCCommand extends CommandAdapter { // TODO: Allow use

    private boolean requiresOwner = false;
    private Runnable runnable;
    private ExecutorService executor;
    private String alias;
    private String example;
    private String usage;
    private boolean privateAllowed = true;

    public AdvancedCCommand setPrivateAllowed(boolean setter) {
        privateAllowed = setter;
        return this;
    }

    public AdvancedCCommand setRequiresOwner(boolean requiresOwner) {
        this.requiresOwner = requiresOwner;
        return this;
    }

    public AdvancedCCommand setRunnable(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    public AdvancedCCommand setExecutor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    public AdvancedCCommand setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public AdvancedCCommand setExample(String example) {
        this.example = example;
        return this;
    }

    public AdvancedCCommand setUsage(String usage) {
        this.usage = usage;
        return this;
    }

    public AdvancedCCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (!privateAllowed && event.isPrivate())
            return;
        super.onMessageReceived(event);
    }

    @Override
    public void onCommand(CommandEvent event) {
        executor.execute(runnable);
    }

    @Override
    public boolean isCommand(String message) {
        return requiresOwner;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public String usage() {
        return usage;
    }

    public String example() {
        return example;
    }

}
