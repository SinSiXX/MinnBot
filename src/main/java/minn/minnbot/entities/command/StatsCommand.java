package minn.minnbot.entities.command;

import java.io.IOException;
import java.util.Vector;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.TimeUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class StatsCommand extends ListenerAdapter implements Command {

	private Logger logger;
	private String prefix;
	private boolean running = false;
	private String about;

	public StatsCommand(Logger logger, String prefix, String about) {
		this.logger = logger;
		this.prefix = prefix;
		this.about = about;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (isCommand(event.getMessage().getContent())) {
			logger.logCommandUse(event.getMessage());
			onCommand(new CommandEvent(event));
		}
	}

	@Override
	public void setLogger(Logger logger) {
		if (logger == null)
			throw new IllegalArgumentException("Logger cannot be null.");
		this.logger = logger;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public void onCommand(CommandEvent event) {
		if (!running) {
			Thread t = new Thread() {
				public void run() {
					String msg;
					try {
						msg = stats(event);
						Message m = event.sendMessageBlocking(msg);
						if (m != null) {
							for (int i = 0; i < 10; i++) {
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {
								}
								m.updateMessage(stats(event));
							}
						}
					} catch (Exception e1) {
						logger.logError(e1);
					}
					running = false;
				}
			};
			t.start();
			running = true;
		} else {
			event.sendMessage("This command is on cooldown, try again in a few moments.");
		}
	}

	private String stats(CommandEvent event) throws IOException {
		int[] stats = logger.getNumbers();
		JDA api = event.event.getJDA();
		String messages = "[Messages][" + stats[0] + "]";
		String commands = "[Commands][" + stats[1] + "]";
		String events = "[Events][" + stats[2] + "]";
		String privateMessages = "[Private-Messages][" + stats[3] + "]";
		String guildMessages = "[Guild-Messages][" + stats[4] + "]";
		String guilds = "[Servers][" + api.getGuilds().size() + "]";
		String users = "[Users][" + api.getUsers().size() + "]";
		String channels = "[Channels][" + api.getPrivateChannels().size() + api.getTextChannels().size()
				+ api.getVoiceChannels().size() + "]";
		String uptime = "[Uptime][" + TimeUtil.uptime(stats[5]) + "]";
		String mem = "[Memory][" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L
				+ "MB / " + Runtime.getRuntime().totalMemory() / 1048576L + "MB]\n";
		// InetAddress discord = InetAddress.getByName("discordapp.com");
		// long prePing = System.currentTimeMillis();
		// discord.isReachable(200);
		// long pastPing = System.currentTimeMillis();
		// String ping = "[Ping][" + ((pastPing - prePing) >= 200 ? ">200" :
		// (pastPing - prePing)) + "]";
		String msg = "```md\nStatistics: " + about + "\n\n[Connection]:\n" + uptime + "\n" + mem
				+ /* "\n" + ping + */ "\n\n[Messages]:\n" + messages + "\n" + privateMessages + "\n" + guildMessages
				+ "\n\n[Usages]:\n" + commands + "\n" + events + "\n\n[Entities]:\n" + guilds + "\n" + users + "\n"
				+ channels + "```";
		return msg;
	}
	@Override
	public boolean isCommand(String message) {
		return message.equalsIgnoreCase(prefix + "stats");
	}

	@Override
	public String usage() {
		return "";
	}

	@Override

	public String getAlias() {
		return "stats";
	}

	public boolean requiresOwner() {
		return false;
	}
}
