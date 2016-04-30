package minn.minnbot;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.audio.MinnPlayer;
import minn.minnbot.entities.command.custom.InviteCommand;
import minn.minnbot.entities.command.custom.MentionedListener;
import minn.minnbot.entities.impl.LoggerImpl;
import minn.minnbot.entities.throwable.Info;
import minn.minnbot.gui.AccountSettings;
import minn.minnbot.gui.MinnBotUserInterface;
import minn.minnbot.manager.*;
import minn.minnbot.manager.impl.*;
import minn.minnbot.util.EvalUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.JDAInfo;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.ReconnectedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.utils.ApplicationUtil;
import org.json.JSONException;
import org.json.JSONObject;

import javax.activation.UnsupportedDataTypeException;
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
public class MinnBot extends ListenerAdapter {

    public final static String VERSION = "Version 2.8.1a";
    public final static String ABOUT = VERSION + " - https://github.com/MinnDevelopment/MinnBot.git";
    public static boolean powersaving = false;
    private static String giphy;
    private static MinnBotUserInterface console;
    private static boolean audio;
    public final String owner; // FIXME
    public final JDA api;
    public final CommandManager handler;
    public final String inviteurl;
    private final String prefix;
    private final Logger logger;
    private final boolean bot;
    private MinnPlayer player;
    private static Guild home;

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
        this.owner = ownerID;
        User uOwner = api.getUserById(owner);
        try {
            log("Owner: " + uOwner.getUsername() + "#" + uOwner.getDiscriminator());
        } catch (NullPointerException e) {
            logger.logThrowable(new NullPointerException(
                    "Owner could not be retrieved from the given id. Do you share a guild with this bot? - Caused by id: \""
                            + ownerID + "\""));
        }
        String invite = getInviteUrl();
        if (!invite.isEmpty())
            this.inviteurl = invite;
        else
            this.inviteurl = inviteurl;
        this.bot = true;
        this.handler = new CommandManager(api, this.logger, owner);
        api.addEventListener(handler);
        api.addEventListener(this);
        log("Powersaving: " + powersaving);
    }

    public synchronized static void launch(MinnBotUserInterface console) throws Exception {
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
            // String inviteUrl = obj.getString("inviteurl"); REMOVED
            String ownerId = obj.getString("owner");
            String giphy = obj.getString("giphy");
            try {
                LoggerImpl.log = obj.getBoolean("log");
                home = api.getGuildById(obj.getString("home"));
            } catch (JSONException ignore) {
            }
            if (giphy != null && !giphy.isEmpty() && !giphy.equalsIgnoreCase("http://api.giphy.com/submit"))
                MinnBot.giphy = giphy;
            MinnBot bot = new MinnBot(pre, ownerId, "", console.logger, api);
            bot.initCommands(api);
            as.setApi(api);
            MinnBotUserInterface.bot = bot;
            Thread.currentThread().setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) bot.getLogger());
            if (audio) {
                api.addEventListener(new MinnAudioManager());
            }
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

    public void onReconnect(ReconnectedEvent event) {


    }

    private String getInviteUrl() {
        String id = ApplicationUtil.getApplicationId(api);
        String invite = ApplicationUtil.getAuthInvite(id);
        if (invite.isEmpty())
            return "";
        return invite.substring(0, invite.length() - 1) + "-1";
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
        return handler.getCommands();
    }

    public void log(String toLog) {
        if (toLog.isEmpty())
            return;
        logger.logThrowable(new Info(toLog));
    }

    public synchronized MinnBot initCommands(JDA api) throws UnknownHostException, UnsupportedDataTypeException {
        List<String> errors = new LinkedList<>();
        try {
            EvalUtil.init();
        } catch (ScriptException e) {
            logger.logThrowable(e);
        }
        PlayingFieldManager.init(api, logger);

        // Add logger to the listeners
        api.addEventListener(logger);

        // Add each command and check for exceptions

        // Operator/Owner Commands

        AtomicReference<CmdManager> manager = new AtomicReference<CmdManager>(new OperatorManager(prefix, logger, this));
        handler.registerManager(manager.get());
        errors.addAll(manager.get().getErrors());


        // User commands

        manager.set(new PublicManager(prefix, logger, this));
        handler.registerManager(manager.get());
        errors.addAll(manager.get().getErrors());

        // Voice
        if (audio) {
            manager.set(new AudioCommandManager(prefix, logger));
            handler.registerManager(manager.get());
            errors.addAll(manager.get().getErrors());
        }
        // Moderation commands

        manager.set(new ModerationCommandManager(prefix, logger));
        handler.registerManager(manager.get());
        errors.addAll(manager.get().getErrors());

        // Role manager

        manager.set(new RoleCommandManager(prefix, logger));
        handler.registerManager(manager.get());
        errors.addAll(manager.get().getErrors());

        // Goofy manager

        manager.set(new GoofyManager(prefix, logger, giphy));
        handler.registerManager(manager.get());
        errors.addAll(manager.get().getErrors());

        if (home != null) {
            registerCommand(new InviteCommand(prefix, logger, home));
            registerCommand(new MentionedListener(logger));
        }

        // Log the outcome
        if (!errors.isEmpty()) {
            console.writeEvent("[ERROR] Some commands could not load up:");
            for (String e : errors) {
                console.writeEvent("[COMMAND] " + e);
            }
        } else {
            log("[COMMANDS] Setup completed without exceptions.\n[JDA] [VERSION] " + JDAInfo.VERSION);
        }
        return this;
    }

    private String registerCommand(Command com) {
        return handler.registerCommand(com);
    }

}
