package minn.minnbot.manager.impl;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.custom.CustomRegister;
import minn.minnbot.entities.command.custom.HelpSplitter;
import minn.minnbot.manager.CmdManager;

import java.util.List;

public class CustomManager extends CmdManager {

    public CustomManager(String prefix, Logger logger) {
        List<Command> commandList = CustomRegister.getCustoms(prefix, logger);
        HelpSplitter splitter = new HelpSplitter("Custom commands", "custom", prefix, false);
        registerCommand(splitter);

        commandList.parallelStream().forEachOrdered(c -> {
            registerCommand(c);
            splitter.add(c);
        });
    }

}
