package minn.minnbot.manager.impl;

import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.MinnBot;
import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.custom.HelpSplitter;
import minn.minnbot.entities.command.goofy.*;
import minn.minnbot.manager.CmdManager;

public class GoofyManager extends CmdManager {

    public GoofyManager(String prefix, Logger logger, String giphy, MinnBot bot) {
        Command com;
        this.logger = logger;
        HelpSplitter splitter = new HelpSplitter("Goofy Commands", "goofy", prefix, false);
        registerCommand(splitter);

        try {
            com = new GifCommand(prefix, logger, giphy);
            registerCommand(com);
            splitter.add(com);
        } catch (UnirestException e) {
            logger.logThrowable(e);
        }

        com = new CatCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);

        com = new MagicBallCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);

        com = new MemeCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);

        com = new YodaCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);

        com = new PyifyCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);

        com = new GoogleCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);

        com = new AraCommand(prefix, logger, bot.api);
        registerCommand(com);
        splitter.add(com);

        com = new ColorCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);

        com = new RandomUserCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);

        com = new RoboCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);

        com = new FactCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);

        com = new LeetifyCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);

        com = new JokeCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);
    }

}
