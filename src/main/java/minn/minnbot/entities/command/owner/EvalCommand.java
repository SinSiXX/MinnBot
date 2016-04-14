package minn.minnbot.entities.command.owner;

import minn.minnbot.MinnBot;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.EvalUtil;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class EvalCommand extends CommandAdapter {

    private User owner;
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
