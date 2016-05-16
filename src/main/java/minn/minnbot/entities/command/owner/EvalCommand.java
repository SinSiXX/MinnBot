package minn.minnbot.entities.command.owner;

import minn.minnbot.MinnBot;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.EvalUtil;

public class EvalCommand extends CommandAdapter {

    private MinnBot bot;

    public EvalCommand(String prefix, Logger logger, MinnBot bot) {
        this.prefix = prefix;
        this.logger = logger;
        this.bot = bot;
    }

    @Override
    public void onCommand(CommandEvent event) {
        EvalUtil.eval(event.allArguments, event.event, bot, (String s) -> {
            if(s != null) {
                event.sendMessage(s);
            } else {
                logger.logThrowable(new UnknownError("Evaluation returned null."));
            }
        });
    }

    @Override
    public String usage() {
        return "`evaluates java code, allows code blocks and quotations`";
    }

    @Override
    public String getAlias() {
        return "eval <code>";
    }

    public boolean requiresOwner() {
        return true;
    }

    @Override
    public String example() {
        return "eval return event.getMessage()";
    }

}
