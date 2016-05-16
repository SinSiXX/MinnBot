package minn.minnbot.manager.impl;

import minn.minnbot.MinnBot;
import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.*;
import minn.minnbot.entities.command.custom.HelpSplitter;
import minn.minnbot.entities.command.owner.FlushCommand;
import minn.minnbot.entities.command.statistics.MessagesCommand;
import minn.minnbot.entities.command.statistics.PingCommand;
import minn.minnbot.entities.command.statistics.StatsCommand;
import minn.minnbot.entities.command.statistics.UptimeCommand;
import minn.minnbot.manager.CmdManager;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

import static minn.minnbot.MinnBot.ABOUT;

public class PublicManager extends CmdManager {

    public PublicManager(String prefix, Logger logger, MinnBot bot) throws UnknownHostException {
        this.logger = logger;
        errors = new LinkedList<>();
        HelpSplitter splitter = new HelpSplitter("Public commands", "public", prefix, false);

        err = new AtomicReference<>(registerCommand(splitter));
        if (!err.get().isEmpty())
            errors.add(err.get());

        Command com = new ExampleCommand(prefix, logger, bot.handler);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new FlushCommand(prefix, logger, bot.owner);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new HelpCommand(prefix, logger, bot.handler, bot.owner);
        err = new AtomicReference<>(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());

        com = new StatsCommand(logger, prefix, ABOUT);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new CheckCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new SayCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new InfoCommand(prefix, logger, bot.api.getUserById(bot.owner), bot.inviteurl, true);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new PingCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new UptimeCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new TagCommand(prefix, logger, bot.api, bot.owner);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

       /*try {
           com = new GifCommand(prefix, logger, giphy);
           err.set(registerCommand(com));
           if (!err.get().isEmpty())
               errors.add(err.get());
           else
               splitter.add(com);
       } catch (UnirestException e) {
           e.printStackTrace();
       }*/

       /*com = new CatCommand(prefix, logger);
       err.set(registerCommand(com));
       if (!err.get().isEmpty())
           errors.add(err.get());
       else
           splitter.add(com);*/

       /*com = new MemeCommand(prefix,logger);
       err.set(registerCommand(com));
       if (!err.get().isEmpty())
           errors.add(err.get());
       else
           splitter.add(com);*/

       /*com = new YodaCommand(prefix,logger);
       err.set(registerCommand(com));
       if (!err.get().isEmpty())
           errors.add(err.get());
       else
           splitter.add(com);*/

        com = new QRCodeCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new UrbanCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

       /*com = new PyifyCommand(prefix, logger);
       err.set(registerCommand(com));
       if (!err.get().isEmpty())
           errors.add(err.get());
       else
           splitter.add(com);*/

        com = new FeedbackCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new MessagesCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
            splitter.add(com);

        com = new PrefixCommand(prefix, logger, bot.owner);
        registerCommand(com);
        splitter.add(com);

        com = new TwitchCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);
    }

}
