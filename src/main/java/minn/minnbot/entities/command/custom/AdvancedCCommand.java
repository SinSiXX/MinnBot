package minn.minnbot.entities.command.custom;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class AdvancedCCommand extends CommandAdapter { // TODO: Allow use

    private boolean requiresOwner = false;
    private Consumer<CommandEvent> consumer;
    private ExecutorService executor;
    private String alias;
    private String internalAlias;
    private String example;
    private String usage;
    private boolean privateAllowed = true;

    public AdvancedCCommand setConsume(Consumer<CommandEvent> consume) {
        this.consumer = consume;
        return this;
    }

    public AdvancedCCommand setInternalAlias(String alias) {
        internalAlias = alias;
        return this;
    }

    public Consumer<CommandEvent> getConsumer() { return consumer; }

    public AdvancedCCommand setPrivateAllowed(boolean setter) {
        privateAllowed = setter;
        return this;
    }

    public AdvancedCCommand setRequiresOwner(boolean requiresOwner) {
        this.requiresOwner = requiresOwner;
        return this;
    }

    public AdvancedCCommand setExecutor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    public AdvancedCCommand setHelpAlias(String alias) {
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
        executor.execute(() -> consumer.accept(event));
    }

    @Override
    public boolean isCommand(String message, List<String> prefixList) {
        String[] p = message.split(" ", 2);
        if(p.length < 1)
            return false;
        if(p[0].equalsIgnoreCase(prefix + internalAlias))
            return true;
        for(String fix : prefixList) {
            if(p[0].equalsIgnoreCase(fix + internalAlias))
                return true;
        }
        return false;
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

    public boolean requiresOwner() { return requiresOwner; }

}
