package minn.minnbot.manager.impl;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.custom.HelpSplitter;
import minn.minnbot.entities.command.moderation.*;
import minn.minnbot.manager.CmdManager;
import net.dv8tion.jda.JDA;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ModerationCommandManager extends CmdManager {

    public ModerationCommandManager(String prefix, Logger logger, JDA api) {
        this.logger = logger;
        List<String> errors = new LinkedList<>();
        HelpSplitter splitter = new HelpSplitter("Moderation commands", "moderation", prefix, false);
        err = new AtomicReference<>(registerCommand(splitter));
        if (!err.get().isEmpty())
            errors.add(err.get());


        Command com = new SilenceCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new TimeoutCommand(prefix, logger, api);
        registerCommand(com);
        splitter.add(com);

        com = new UnsilenceCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new PurgeCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new SoftbanCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new ClearCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new NickerCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new SlowmodeCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);

        this.errors = errors;
    }
}
