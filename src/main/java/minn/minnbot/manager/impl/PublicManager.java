package minn.minnbot.manager.impl;

import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.MinnBot;
import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.*;
import minn.minnbot.entities.command.custom.HelpSplitter;
import minn.minnbot.manager.CmdManager;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static minn.minnbot.MinnBot.ABOUT;

public class PublicManager extends CmdManager {

   public PublicManager(String prefix, Logger logger, MinnBot bot, String giphy) throws UnknownHostException {
       this.logger = logger;
       List<String> errors = new LinkedList<>();
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

       com = new InfoCommand(prefix, logger, bot.owner, bot.inviteurl, true);
       err.set(registerCommand(com));
       if (!err.get().isEmpty())
           errors.add(err.get());
       else
           splitter.add(com);

       com = new ResponseCommand(prefix, logger);
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

       com = new QRCodeCommand(prefix, logger);
       err.set(registerCommand(com));
       if (!err.get().isEmpty())
           errors.add(err.get());
       else
           splitter.add(com);

       com = new FeedbackCommand(prefix,logger);
       err.set(registerCommand(com));
       if (!err.get().isEmpty())
           errors.add(err.get());
       else
           splitter.add(com);

       this.errors = errors;
   }

}
