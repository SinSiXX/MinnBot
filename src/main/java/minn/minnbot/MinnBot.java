package minn.minnbot;

import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.audio.MinnPlayer;
import minn.minnbot.entities.command.*;
import minn.minnbot.entities.command.audio.CurrentCommand;
import minn.minnbot.entities.command.audio.JoinCommand;
import minn.minnbot.entities.command.audio.LeaveVoiceCommand;
import minn.minnbot.entities.command.audio.PlayCommand;
import minn.minnbot.entities.command.custom.HelpSplitter;
import minn.minnbot.entities.command.owner.*;
import minn.minnbot.entities.command.roles.CopyRoleCommand;
import minn.minnbot.entities.command.roles.CreateRoleCommand;
import minn.minnbot.entities.command.roles.EditRoleCommand;
import minn.minnbot.entities.throwable.Info;
import minn.minnbot.gui.AccountSettings;
import minn.minnbot.gui.MinnBotUserInterface;
import minn.minnbot.manager.CommandManager;
import minn.minnbot.manager.ModLogManager;
import minn.minnbot.util.EvalUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import org.json.JSONObject;

import javax.script.ScriptException;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.UnexpectedException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unused")
public class MinnBot {

    public final static String VERSION = "Version 1.8b";
    public final static String ABOUT = VERSION + " - https://github.com/MinnDevelopment/MinnBot.git";
    public static boolean powersaving = false;
    private static String giphy;
    private static MinnBotUserInterface console;
    public final User owner;
    public final JDA api;
    public final CommandManager handler;
    private final String prefix;
    private final Logger logger;
    private final String inviteurl;
    private final boolean bot;
    private static boolean audio;
    private MinnPlayer player;

    public MinnBot(String prefix, String ownerID, String inviteurl, Logger logger, JDA api)
            throws Exception {
        if (prefix.contains(" "))
            throw new IllegalArgumentException("Prefix contains illegal characters. (i.e. space)");
        this.api = api;
        if (!waitForReady(this.api))
            throw new UnexpectedException("Guilds were unreachable");
        // mb-mod-log
        new ModLogManager(api);

        this.logger = logger;

        this.prefix = prefix;
        log("Prefix: " + prefix);
        this.owner = this.api.getUserById(ownerID);
        try {
            log("Owner: " + owner.getUsername() + "#" + owner.getDiscriminator());
        } catch (NullPointerException e) {
            logger.logThrowable(new NullPointerException(
                    "Owner could not be retrieved from the given id. Do you share a guild with this bot? - Caused by id: \""
                            + ownerID + "\""));
        }
        this.inviteurl = inviteurl;
        this.bot = true;
        this.handler = new CommandManager(api, this.logger, owner);
        api.addEventListener(handler);
        log("Powersaving: " + powersaving);
    }

    public static void launch(MinnBotUserInterface console) throws Exception {
        MinnBot.console = console;
        AccountSettings as = new AccountSettings(console);
        console.setAccountSettings(as);
        try {
            JSONObject obj = new JSONObject(new String(Files.readAllBytes(Paths.get("BotConfig.json"))));
            String token = obj.getString("token").replace(" ", "");
            audio = obj.getBoolean("audio");
            JDA api;
            api = new JDABuilder().setBotToken(token).setAudioEnabled(audio).setAutoReconnect(true).buildBlocking();
            try {
                powersaving = obj.getBoolean("powersaving");
            } catch (Exception ignored) {
            }
            String pre = obj.getString("prefix");
            String inviteUrl = obj.getString("inviteurl");
            String ownerId = obj.getString("owner");
            String giphy = obj.getString("giphy");
            if (giphy != null && !giphy.isEmpty() && !giphy.equalsIgnoreCase("http://api.giphy.com/submit"))
                MinnBot.giphy = giphy;
            MinnBot bot = new MinnBot(pre, ownerId, inviteUrl, console.logger, api);
            bot.initCommands(api);
            as.setApi(api);
            MinnBotUserInterface.bot = bot;
            Thread.currentThread().setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) bot.getLogger());
            bot.log("Setup completed.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().isEmpty())
                console.writeEvent("The config was not populated.\n" + "Please enter a bot token.");
            e.printStackTrace();
            throw e;
        } catch (LoginException e) {
            console.writeEvent("The provided login information was invalid.\n"
                    + "Please provide a valid token or email and password combination.");
            throw e;
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            JSONObject obj = new JSONObject();
            obj.put("prefix", "");
            obj.put("owner", "");
            obj.put("token", "");
            obj.put("inviteurl", "");
            obj.put("powersaving", false);
            obj.put("audio", false);
            obj.put("giphy", "http://api.giphy.com/submit");
            try {
                Files.write(Paths.get("BotConfig.json"), obj.toString(4).getBytes());
                console.writeEvent(
                        "No config file was found. BotConfig.json has been generated.\nPlease fill the fields with correct information!");
            } catch (IOException e1) {
                console.writeEvent("No config file was found and we failed to generate one.");
                e1.printStackTrace();
            }
            throw e;
        }

    }

    public static void main(String[] a) {
        console = new MinnBotUserInterface();
        console.setVisible(true);
        console.pack();
    }

    public Logger getLogger() {
        return logger;
    }

    private boolean waitForReady(JDA api) {
        if (api == null)
            throw new IllegalArgumentException("JDA instance can not be null.");
        try {
            api.getGuilds().parallelStream().forEach((Guild t) -> {
                while (!t.isAvailable())
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
            });
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public List<Command> getCommands() {
        return handler.commands;
    }

    public void log(String toLog) {
        if (toLog.isEmpty())
            return;
        logger.logThrowable(new Info(toLog));
    }

    public MinnBot initCommands(JDA api) throws UnknownHostException {
        List<String> errors = new LinkedList<>();
        try {
            EvalUtil.init();
        } catch (ScriptException e) {
            logger.logThrowable(e);
        }

        // Add logger to the listeners
        api.addEventListener(logger);

        // Add each command and check for exceptions

        // Operator/Owner Commands

        HelpSplitter splitter = new HelpSplitter("Operator", "op", prefix, true);
        AtomicReference<String> err = new AtomicReference<>(registerCommand(splitter));
        if (!err.get().isEmpty())
            errors.add(err.get());

        Command com = new HelpCommand(prefix, logger, handler.commands, owner);
        err = new AtomicReference<>(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());

        com = new EvalCommand(prefix, logger, this);
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

        com = new FlushCommand(prefix, logger);
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

        // User commands

        splitter = new HelpSplitter("Public commands", "public", prefix, false);
        err.set(registerCommand(splitter));
        if (!err.get().isEmpty())
            errors.add(err.get());

        com = new ExampleCommand(prefix, logger, handler.commands);
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

        com = new InfoCommand(prefix, logger, owner, inviteurl, bot);
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

        com = new TagCommand(prefix, logger, api, owner);
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

        // Voice
        if(audio) {
            splitter = new HelpSplitter("Voice commands", "voice", prefix, false);
            err.set(registerCommand(splitter));
            if (!err.get().isEmpty())
                errors.add(err.get());

            com = new LeaveVoiceCommand(prefix, logger);
            err.set(registerCommand(com));
            if (!err.get().isEmpty())
                errors.add(err.get());
            else
                splitter.add(com);

            PlayCommand pCom = new PlayCommand(prefix, logger);
            err.set(registerCommand(pCom));
            if (!err.get().isEmpty())
                errors.add(err.get());
            else {
                splitter.add(pCom);
                player = pCom.getPlayer();
            }
            com = new JoinCommand(prefix, logger, player);
            err.set(registerCommand(com));
            if (!err.get().isEmpty())
                errors.add(err.get());
            else
                splitter.add(com);

            com = new CurrentCommand(prefix, logger, player);
            err.set(registerCommand(com));
            if (!err.get().isEmpty())
                errors.add(err.get());
            else
                splitter.add(com);

        }
        // Moderation commands

        splitter = new HelpSplitter("Moderation commands", "moderation", prefix, false);
        err.set(registerCommand(splitter));
        if (!err.get().isEmpty())
            errors.add(err.get());

        com = new SilenceCommand(prefix, logger);
        err.set(registerCommand(com));
        if (!err.get().isEmpty())
            errors.add(err.get());
        else
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

        // Role manager

        splitter = new HelpSplitter("Role managing commands", "roles", prefix, false);
        err.set(registerCommand(splitter));
        if (!err.get().isEmpty())
            errors.add(err.get());

        com = new CreateRoleCommand(logger, prefix);
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

        // Log the outcome
        if (!errors.isEmpty()) {
            console.writeEvent("[ERROR] Some commands could not load up:");
            for (String e : errors) {
                console.writeEvent("[COMMAND] " + e);
            }
        } else {
            log("[COMMANDS] Setup completed without exceptions.");
        }
        return this;
    }

    private String registerCommand(Command com) {
        return handler.registerCommand(com);
    }

}
