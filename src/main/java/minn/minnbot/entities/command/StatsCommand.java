package minn.minnbot.entities.command;

import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.TimeUtil;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.io.IOException;

public class StatsCommand extends CommandAdapter {

	private boolean running = false;
	private String about;
	private long ping;

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
	public void onCommand(CommandEvent event) {
		if (!running) {
			Thread t = new Thread() {
				public void run() {
					String msg;
					try {
						msg = stats(event, -1);
						long start = System.currentTimeMillis();
						Message m = event.sendMessageBlocking(msg);
						if (m != null) {
							m.updateMessage(msg.replace("{ping}", (System.currentTimeMillis() - start) + "ms"));
							for (int i = 0; i < 10; i++) {
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {
								}
								msg = stats(event, ping);
								start = System.currentTimeMillis();
								m.updateMessage(msg);
								ping = System.currentTimeMillis() - start;
							}
						}
					} catch (Exception e1) {
						logger.logThrowable(e1);
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

	private String stats(CommandEvent event, long ms) throws IOException {
		int[] stats = logger.getNumbers();
		JDA api = event.event.getJDA();
		/* Ping */String ping = (ms < 1) ? "[Ping][{ping}]" : "[Ping][" + ms + "]";
		/* Mess */String messages = "[Messages][" + stats[0] + "]";
		/* Comm */String commands = "[Commands][" + stats[1] + "]";
		/* Evnt */String events = "[Events][" + stats[2] + "]";
		/* Priv */String privateMessages = "[Private-Messages][" + stats[3] + "]";
		/* Gild */String guildMessages = "[Guild-Messages][" + stats[4] + "]";
		/* Glds */String guilds = "[Servers][" + api.getGuilds().size() + "]";
		/* Usrs */String users = "[Users][" + api.getUsers().size() + "]";
		/* Chns */String channels = "[Channels]: [Private][" + api.getPrivateChannels().size() + "] [Text][" + api.getTextChannels().size() + "] [Voice]["
				+ api.getVoiceChannels().size() + "]";
		/* Uptm */String uptime = "[Uptime][" + TimeUtil.uptime(stats[5]) + "]";
		/* mems */String mem = "[Memory][" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L
				+ "MB / " + Runtime.getRuntime().totalMemory() / 1048576L + "MB]";
		/* Rsps */String responses = "[Responses][" + event.event.getJDA().getResponseTotal() + "]";

		String msg = "```md\nStatistics: " + about + "\n\n[Connection]:\n" + uptime + "\n" + mem
				+ "\n" + ping + "\n\n[Messages]:\n" + messages + "\n" + privateMessages + "\n" + guildMessages
				+ "\n\n[Usages]:\n" + commands + "\n" + responses + "\n" + events + "\n\n[Entities]:\n" + guilds + "\n" + users + "\n"
				+ channels + "```";
		return msg;
	}
	@Override
	public boolean isCommand(String message) {
		return message.equalsIgnoreCase(prefix + "stats");
	}

	public String getAlias() {
		return "stats";
	}

}
