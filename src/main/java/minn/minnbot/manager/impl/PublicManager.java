package minn.minnbot.manager.impl;

import minn.minnbot.MinnBot;
import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.*;
import minn.minnbot.entities.command.custom.HelpSplitter;
import minn.minnbot.entities.command.owner.FlushCommand;
import minn.minnbot.entities.command.statistics.*;
import minn.minnbot.manager.CmdManager;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

import static minn.minnbot.MinnBot.ABOUT;

public class PublicManager extends CmdManager {

    public PublicManager(String prefix, Logger logger, MinnBot bot) throws UnknownHostException {
        this.logger = logger;
        errors = new LinkedList<>();
        splitter = new HelpSplitter("Public commands", "public", prefix, false);

        err = new AtomicReference<>(registerCommand(splitter));
        if (!err.get().isEmpty())
            errors.add(err.get());

        Command com = new ExampleCommand(prefix, logger, bot.handler);
        err.set(registerCommand(com));
        splitter.add(com);

        com = new FlushCommand(prefix, logger, bot.owner);
        err.set(registerCommand(com));
        splitter.add(com);

        com = new HelpCommand(prefix, logger, bot.handler, bot.owner);
        err = new AtomicReference<>(registerCommand(com));

        com = new StatsCommand(logger, prefix, ABOUT);
        err.set(registerCommand(com));
        splitter.add(com);

        com = new CheckCommand(prefix, logger);
        err.set(registerCommand(com));
        splitter.add(com);

        com = new SayCommand(prefix, logger);
        err.set(registerCommand(com));
        splitter.add(com);

        com = new PingCommand(prefix, logger);
        err.set(registerCommand(com));
        splitter.add(com);

        com = new UptimeCommand(prefix, logger);
        err.set(registerCommand(com));
        splitter.add(com);

        com = new TagCommand(prefix, logger, bot.owner);
        err.set(registerCommand(com));
        splitter.add(com);

        com = new QRCodeCommand(prefix, logger);
        err.set(registerCommand(com));
        splitter.add(com);

        com = new UrbanCommand(prefix, logger);
        err.set(registerCommand(com));
        splitter.add(com);

        com = new MessagesCommand(prefix, logger);
        err.set(registerCommand(com));
        splitter.add(com);

        /*com = new PrefixCommand(prefix, logger, bot.owner);
        registerCommand(com);
        splitter.add(com);*/

        com = new TwitchCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);

        com = new FindCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);

        com = new CreationDateCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);

        com = new JoinDateCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);

        com = new EmoteCommand(prefix, logger);
        registerCommand(com);
        splitter.add(com);
    }

    private HelpSplitter splitter;

    public void init(MinnBot bot) {
        Command com = new InfoCommand(bot.prefix, logger, bot.api.getUserById(bot.owner), bot.inviteurl, true);
        err.set(registerCommand(com));
        splitter.add(com);
    }

}
