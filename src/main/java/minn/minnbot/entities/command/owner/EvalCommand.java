package minn.minnbot.entities.command.owner;

import minn.minnbot.MinnBot;
import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.EvalUtil;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class EvalCommand extends ListenerAdapter implements Command {

    private User owner;
    private String prefix;
    private Logger logger;
    private MinnBot bot;

    public EvalCommand(User owner, String prefix, Logger logger, MinnBot bot) {
        this.owner = owner;
        this.prefix = prefix;
        this.logger = logger;
        this.bot = bot;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor() == owner && isCommand(event.getMessage().getContent())) {
            logger.logCommandUse(event.getMessage());
            onCommand(new CommandEvent(event));
        }
    }

    @Override
    public void setLogger(Logger logger) {
        if (logger == null)
            throw new IllegalArgumentException("Logger cannot be null.");
        this.logger = logger;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void onCommand(CommandEvent event) {
        EvalUtil.eval(event.allArguments, event.event, bot, (String s) -> {
            if(s != null) {
                event.sendMessage(s);
            } else {
                logger.logError(new UnknownError("Evaluation returned null."));
            }
        });
    }

    @Override
    public boolean isCommand(String message) {
        message = message.toLowerCase();
        return message.startsWith(prefix + "eval ");
    }

    @Override
    public String usage() {
        return "`evaluates java code, allows code blocks and quotations`";
    }

    @Override
    public String getAlias() {
        return "eval code";
    }

    public boolean requiresOwner() {
        return true;
    }

}
