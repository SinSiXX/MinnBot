package minn.minnbot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.UnexpectedException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.security.auth.login.LoginException;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.*;
import minn.minnbot.entities.command.owner.*;
import minn.minnbot.util.TimeUtil;
import minn.minnbot.gui.AccountSettings;
import minn.minnbot.gui.MinnBotUserInterface;
import minn.minnbot.manager.CommandManager;
import org.json.JSONObject;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class MinnBot extends ListenerAdapter {

	public final static String about = "Version DEV - https://github.com/MinnDevelopment/jMinnBot.git";

	public MinnBot(String prefix, String ownerID, String inviteurl, boolean bot, Logger logger, JDA api)
			throws UnexpectedException {
		if (!bot) {
			throw new IllegalArgumentException();
		}
		this.api = api;
		if (!waitForReady(this.api))
			throw new UnexpectedException("Guilds were unreachable");
		this.logger = logger;
		this.prefix = prefix;
		log("Prefix: " + prefix);
		this.owner = this.api.getUserById(ownerID);
		try {
			log("Owner: " + owner.getUsername() + "#" + owner.getDiscriminator());
		} catch (NullPointerException e) {
			logger.logError(new NullPointerException(
					"Owner could not be retrieved from the given id. Do you share a guild with this bot? - Caused by id: \""
							+ ownerID + "\""));
		}
		this.inviteurl = inviteurl;
		this.bot = true;
		this.handler = new CommandManager(api, this.logger);
		api.addEventListener(handler);
	}

	private final String prefix;
	public final User owner;
	private final Logger logger;
	private final String inviteurl;
	private final boolean bot;
	public final JDA api;
	private static MinnBotUserInterface console;
	public final CommandManager handler;


	private boolean waitForReady(JDA api) {
		if (api == null)
			throw new IllegalArgumentException("JDA instance can not be null.");
		try {
			api.getGuilds().parallelStream().forEach((Guild t) -> {
				while (!t.isAvailable())
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
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
		String stamp = TimeUtil.timeStamp();
		console.writeln(stamp + "[MINNBOT] " + toLog);
	}

	public MinnBot initCommands(JDA api) {
		List<String> errors = new LinkedList<>();
		// Add logger to the listeners
		api.addEventListener(logger);

		// Add each command and check for exceptions

		// Operator/Owner Commands
		Command com = new HelpCommand(prefix, logger, handler.commands);
		AtomicReference<String> err = new AtomicReference<>(registerCommand(com));
		if (!err.get().isEmpty())
			errors.add(err.get());

		com = new NameCommand(owner, prefix, logger);
		err.set(registerCommand(com));
		if (!err.get().isEmpty())
			errors.add(err.get());

		com = new GameCommand(prefix, logger, owner);
		err.set(registerCommand(com));
		if (!err.get().isEmpty())
			errors.add(err.get());

		com = new ShutdownCommand(prefix, owner, logger);
		err.set(registerCommand(com));
		if (!err.get().isEmpty())
			errors.add(err.get());

		com = new DebugCommand(owner, prefix, logger);
		err.set(registerCommand(com));
		if (!err.get().isEmpty())
			errors.add(err.get());

		com = new LeaveCommand(prefix, logger, owner);
		err.set(registerCommand(com));
		if (!err.get().isEmpty())
			errors.add(err.get());

		com = new FlushCommand(prefix, logger, owner);
		err.set(registerCommand(com));
		if (!err.get().isEmpty())
			errors.add(err.get());

		// User commands
		com = new StatsCommand(logger, prefix, about);
		err.set(registerCommand(com));
		if (!err.get().isEmpty())
			errors.add(err.get());

		com = new SilenceCommand(prefix, logger);
		err.set(registerCommand(com));
		if (!err.get().isEmpty())
			errors.add(err.get());

		com = new UnsilenceCommand(prefix, logger);
		err.set(registerCommand(com));
		if (!err.get().isEmpty())
			errors.add(err.get());

		com = new CheckCommand(prefix, logger);
		err.set(registerCommand(com));
		if (!err.get().isEmpty())
			errors.add(err.get());

		com = new SayCommand(prefix, logger);
		err.set(registerCommand(com));
		if (!err.get().isEmpty())
			errors.add(err.get());

		com = new PurgeCommand(prefix, logger);
		err.set(registerCommand(com));
		if (!err.get().isEmpty())
			errors.add(err.get());

		com = new ClearCommand(prefix, logger);
		err.set(registerCommand(com));
		if (!err.get().isEmpty())
			errors.add(err.get());

		com = new InfoCommand(prefix, logger, owner, inviteurl, bot);
		err.set(registerCommand(com));
		if (!err.get().isEmpty())
			errors.add(err.get());

		// Log the outcome
		if (!errors.isEmpty()) {
			console.writeError("[ERROR] Some commands could not load up:");
			for (String e : errors) {
				console.writeError("[COMMAND] " + e);
			}
		} else {
			log("[COMMANDS] Setup completed without exceptions.");
		}
		return this;
	}

	public String registerCommand(Command com) {
		return handler.registerCommand(com);
	}

	public static void launch(MinnBotUserInterface console) throws IOException, InterruptedException, LoginException {
		MinnBot.console = console;
		AccountSettings as = new AccountSettings(console);
		console.setAccountSettings(as);
		try {
			JSONObject obj = new JSONObject(new String(Files.readAllBytes(Paths.get("BotConfig.json"))));
			String token = obj.getString("token").replace(" ", "");
			boolean isBot = false;
			JDA api;
			try {
				api = new JDABuilder().setBotToken(token).setAudioEnabled(false).setAutoReconnect(true).buildBlocking();
				isBot = true;
			} catch (LoginException e) {
				try {
					String email = obj.getString("email");
					String pass = obj.getString("password");
					api = new JDABuilder().setEmail(email).setPassword(pass).setAudioEnabled(false)
							.setAutoReconnect(true).buildBlocking();
				} catch (Exception e1) {
					throw e;
				}
			}
			String pre = obj.getString("prefix");
			String inviteUrl = obj.getString("inviteurl");
			String ownerId = obj.getString("owner");
			MinnBot bot = new MinnBot(pre, ownerId, inviteUrl, isBot, console.logger, api);
			api.addEventListener(bot.initCommands(api));
			as.setApi(api);
			MinnBotUserInterface.bot = bot;
		} catch (IllegalArgumentException e) {
			if (e.getMessage().isEmpty())
				console.writeError("The config was not populated.\n" + "Please enter a bot token.");
			e.printStackTrace();
			throw e;
		} catch (LoginException e) {
			console.writeError("The provided login information was invalid.\n"
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
			try {
				Files.write(Paths.get("BotConfig.json"), obj.toString(4).getBytes());
				console.writeError(
						"No config file was found. BotConfig.json has been generated.\nPlease fill the fields with correct information!");
			} catch (IOException e1) {
				console.writeError("No config file was found and we failed to generate one.");
				e1.printStackTrace();
			}
			throw e;
		}

	}

	public static void relaunch(MinnBotUserInterface console)
			throws IOException, LoginException, InterruptedException {
		MinnBot.console = console;
		AccountSettings as = new AccountSettings(console);
		console.setAccountSettings(as);
		try {
			JSONObject obj = new JSONObject(new String(Files.readAllBytes(Paths.get("BotConfig.json"))));
			String token = obj.getString("token").replace(" ", "");
			boolean isBot = false;
			JDA api;
			try {
				api = new JDABuilder().setBotToken(token).setAudioEnabled(false).setAutoReconnect(true).buildBlocking();
				isBot = true;
			} catch (LoginException e) {
				try {
					String email = obj.getString("email");
					String pass = obj.getString("password");
					api = new JDABuilder().setEmail(email).setPassword(pass).setAudioEnabled(false)
							.setAutoReconnect(true).buildBlocking();
				} catch (Exception e1) {
					throw e;
				}
			}
			String pre = obj.getString("prefix");
			String ownerId = obj.getString("owner");
			String inviteUrl = obj.getString("inviteurl");
			MinnBot bot = new MinnBot(pre, ownerId, inviteUrl, isBot, console.logger, api);
			api.addEventListener(bot.initCommands(api));
			as.setApi(api);
			MinnBotUserInterface.bot = bot;
		} catch (IllegalArgumentException e) {
			if (e.getMessage().isEmpty())
				console.writeError("The config was not populated.\n" + "Please enter a bot token.");
			e.printStackTrace();
			throw e;
		} catch (LoginException e) {
			console.writeError("The provided login information was invalid.\n"
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
			try {
				Files.write(Paths.get("BotConfig.json"), obj.toString(4).getBytes());
				console.writeError(
						"No config file was found. BotConfig.json has been generated.\nPlease fill the fields with correct information!");
			} catch (IOException e1) {
				console.writeError("No config file was found and we failed to generate one.");
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

}
