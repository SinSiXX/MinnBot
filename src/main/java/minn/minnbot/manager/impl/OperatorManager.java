package minn.minnbot.manager.impl;

import minn.minnbot.MinnBot;
import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.custom.HelpSplitter;
import minn.minnbot.entities.command.owner.*;
import minn.minnbot.manager.CmdManager;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

public class OperatorManager extends CmdManager {

    public OperatorManager(String prefix, Logger logger, MinnBot bot) {
        this.logger = logger;
        this.ownerOnly = true;
        errors = new LinkedList<>();
        HelpSplitter splitter = new HelpSplitter("Operator commands", "op", prefix, true);
        err = new AtomicReference<>(registerCommand(splitter));
        if (!err.get().isEmpty())
            errors.add(err.get());

        Command com = new EvalCommand(prefix, logger, bot);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new NameCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new GameCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new ShutdownCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new DebugCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new LeaveCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new IgnoreCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new ResetCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new RepairPlayersCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new DisableCommand(prefix, logger, bot.handler);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new EnableCommand(prefix, logger, bot.handler);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new NickResetCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new AvatarCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);

        com = new TrackAvatarCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);
    }

}
