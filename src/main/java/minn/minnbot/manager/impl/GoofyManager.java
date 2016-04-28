package minn.minnbot.manager.impl;

import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.custom.HelpSplitter;
import minn.minnbot.entities.command.goofy.*;
import minn.minnbot.manager.CmdManager;

import java.util.concurrent.atomic.AtomicReference;

public class GoofyManager extends CmdManager {

    public GoofyManager(String prefix, Logger logger, String giphy) {
        Command com;
        this.logger = logger;
        HelpSplitter splitter = new HelpSplitter("Goofy Commands", "goofy", prefix, false);
        err = new AtomicReference<>(registerCommand(splitter));
        if (!err.get().isEmpty())
            errors.add(err.get());

        try {
            com = new GifCommand(prefix, logger, giphy);
            err.set(registerCommand(com));
            if (!err.get().isEmpty())
                errors.add(err.get());
            else
                splitter.add(com);
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        com = new CatCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new MemeCommand(prefix,logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new YodaCommand(prefix,logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new PyifyCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

    }

}
