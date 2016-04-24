package minn.minnbot.manager.impl;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.custom.HelpSplitter;
import minn.minnbot.entities.command.roles.CopyRoleCommand;
import minn.minnbot.entities.command.roles.CreateRoleCommand;
import minn.minnbot.entities.command.roles.EditRoleCommand;
import minn.minnbot.manager.CmdManager;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RoleCommandManager extends CmdManager {

    public RoleCommandManager(String prefix, Logger logger) {
        this.logger = logger;
        List<String> errors = new LinkedList<>();
        HelpSplitter splitter = new HelpSplitter("Role managing commands", "roles", prefix, false);
        err = new AtomicReference<>(registerCommand(splitter));
        if (!err.get().isEmpty())
            errors.add(err.get());

        Command com = new CreateRoleCommand(logger, prefix);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new CopyRoleCommand(logger, prefix);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new EditRoleCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        this.errors = errors;

    }
}
