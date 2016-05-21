package minn.minnbot;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.PublicLog;
import minn.minnbot.entities.audio.MinnPlayer;
import minn.minnbot.entities.command.TagCommand;
import minn.minnbot.entities.impl.IIgnoreListener;
import minn.minnbot.entities.throwable.Info;
import minn.minnbot.gui.AccountSettings;
import minn.minnbot.gui.MinnBotUserInterface;
import minn.minnbot.manager.*;
import minn.minnbot.manager.impl.*;
import minn.minnbot.util.EvalUtil;
import minn.minnbot.util.IgnoreUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.hooks.EventListener;
import net.dv8tion.jda.utils.ApplicationUtil;
import org.json.JSONObject;

import javax.activation.UnsupportedDataTypeException;
import javax.script.ScriptException;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unused")
public class MinnBot implements EventListener {

    public final static String VERSION = "Version 4.0a";
    public final static String ABOUT = VERSION + " - https://github.com/MinnDevelopment/MinnBot.git";
    private static String giphy;
    private static MinnBotUserInterface console;
    private static boolean audio;
    public final String owner;
    public JDA api;
    public final CommandManager handler;
    public String inviteurl;
    private static IIgnoreListener ignoreListener;
    public final String prefix;
    private final Logger logger;
    private MinnPlayer player;
    private static Guild home;
    private boolean ready = false;
    private static JSONObject config = null;
    private static PublicManager tmp;


    public void onReady(ReadyEvent event) {
        log("Setting up api related config...");
        Thread t = new Thread(() -> {
            TagCommand.initTags(event.getJDA(), logger);
            PublicLog.init(api, logger::logThrowable);
            this.api = event.getJDA();
            if (config.has("home"))
                home = api.getGuildById(config.getString("home"));
            new ModLogManager(api);
            User uOwner = api.getUserById(owner);
            try {
                log("Owner: " + uOwner.getUsername() + "#" + uOwner.getDiscriminator());
            } catch (NullPointerException e) {
                logger.logThrowable(new NullPointerException(
                        "Owner could not be retrieved from the given id. Do you share a guild with this bot? - Caused by id: \""
                                + owner + "\""));
            }
            this.handler.setApi(event.getJDA());
            try {
                initCommands(api);
            } catch (UnknownHostException | UnsupportedDataTypeException e) {
                logger.logThrowable(e);
            }
            AccountSettings as = new AccountSettings(console);
            console.setAccountSettings(as);
            as.setApi(api);
            if (audio) api.addEventListener(new MinnAudioManager());
            console.setTitle(api.getSelfInfo().getUsername() + "#" + api.getSelfInfo().getDiscriminator()); // so you know which one is logged in!
            inviteurl = getInviteUrl();
            tmp.init(this);
            logger.logInfo("Setup completed.");
        });
        t.setDaemon(false);
        t.setPriority(Thread.MAX_PRIORITY);
        t.setName("onReady...");
        t.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) logger);
        t.start();
    }

    public MinnBot(String prefix, String ownerID, Logger logger)
            throws Exception {
        if (prefix.contains(" "))
            throw new IllegalArgumentException("Prefix contains illegal characters. (i.e. space)");
        this.logger = logger;
        this.prefix = prefix;
        this.owner = ownerID;
        this.handler = new CommandManager(this.logger, owner);
        log(String.format("\nConfig[\n\tPrefix: %s,\n\tOwner: %s\n\t]", prefix, ownerID));
        String token = config.getString("token").trim();
        audio = config.getBoolean("audio");
        api = new JDABuilder().setBotToken(token).addListener(this).setAudioEnabled(audio).setAutoReconnect(true).buildAsync();
        initCommands();
    }

    public synchronized static void launch(MinnBotUserInterface console) throws Exception { // DON'T LOOK
        MinnBot.console = console;
        try {
            config = new JSONObject(new String(Files.readAllBytes(Paths.get("BotConfig.json"))));
            String pre = config.getString("prefix").trim();
            String ownerId = config.getString("owner").trim();
            String giphy = config.getString("giphy").trim();
            MinnBot bot = new MinnBot(pre, ownerId, console.logger);
            MinnBot.giphy = giphy;
            MinnBotUserInterface.bot = bot;
            Thread.currentThread().setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) bot.getLogger());
            if (ignoreListener == null) {
                ignoreListener = new IIgnoreListener();
                IgnoreUtil.addListener(ignoreListener);
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage().isEmpty())
                console.writeEvent("The config was not populated.\nPlease enter a bot token.");
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
            obj.put("log", true);
            obj.put("audio", false);
            obj.put("giphy", "dc6zaTOxFJmzC");
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

    private String getInviteUrl() {
        String id = ApplicationUtil.getApplicationId(api);
        String invite = ApplicationUtil.getAuthInvite(id);
        if (invite.isEmpty())
            return "";
        return invite.substring(0, invite.length() - 1) + "-1";
    }

    public static void main(String... a) {
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
        return handler.getCommands();
    }

    public void log(String toLog) {
        if (toLog.isEmpty())
            return;
        logger.logInfo(new Info(toLog));
    }

    public synchronized MinnBot initCommands(JDA api) throws UnknownHostException, UnsupportedDataTypeException {
        PlayingFieldManager.init(api, logger);

        // Add logger to the listeners
        api.addEventListener(logger);

        AtomicReference<CmdManager> manager = new AtomicReference<>(new OperatorManager(prefix, logger, this));
        handler.registerManager(manager.get());

        // Moderation commands

        manager.set(new ModerationCommandManager(prefix, logger, api));
        handler.registerManager(manager.get());

        return this;
    }

    private synchronized MinnBot initCommands() throws UnknownHostException, UnsupportedDataTypeException {
        try {
            EvalUtil.init();
        } catch (ScriptException e) {
            logger.logThrowable(e);
        }
        // User commands
        tmp = new PublicManager(prefix, logger, this);
        AtomicReference<CmdManager> manager = new AtomicReference<>(tmp);
        handler.registerManager(manager.get());

        // Voice
        if (audio) {
            manager.set(new AudioCommandManager(prefix, logger));
            handler.registerManager(manager.get());
        }

        manager.set(new RoleCommandManager(prefix, logger));
        handler.registerManager(manager.get());

        manager.set(new GoofyManager(prefix, logger, giphy, this));
        handler.registerManager(manager.get());

        manager.set(new CustomManager(prefix, logger));
        handler.registerManager(manager.get());
        return this;
    }

    private String registerCommand(Command com) {
        //noinspection deprecation
        return handler.registerCommand(com);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof ReadyEvent) {
            onReady((ReadyEvent) event);
            event.getJDA().removeEventListener(this);
        }
    }
}
