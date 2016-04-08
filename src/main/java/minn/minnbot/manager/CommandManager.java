package minn.minnbot.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import minn.minnbot.entities.Command;
import minn.minnbot.entities.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class CommandManager extends ListenerAdapter {

	public List<Command> commands = new LinkedList<Command>();
	@SuppressWarnings("unused")
	private JDA api;
	
	private Logger logger;

	public void onMessageReceived(MessageReceivedEvent event) {
		Thread t = new Thread() {
			public void run() {
				for (Command c : commands) {
					c.onMessageReceived(event);
				}
			}
		};
		t.start();
	}

	public CommandManager(JDA api, Logger logger) {
		this.api = api;
		this.logger = logger;
	}

	public String registerCommand(Command com) {
		try {
			commands.add(com);
		} catch (Exception e) {
			return com.getClass().getSimpleName() + ": " + e;
		}
		return "";
	}

	public boolean removeCommand(Command com) {
		for (Command c : commands) {
			if (com.getAlias().equals(c.getAlias())) {
				commands.remove(c);
				return true;
			}
		}
		return false;
	}

	private JSONObject jsonfy(Command command) {
		String alias = command.getAlias();
//		String name = command.getClass().getSimpleName();
		String usage = command.usage();
		boolean ownerOnly = command.requiresOwner();
		JSONObject obj = new JSONObject();
//		obj.put("name", name);
		obj.put("aliase", alias);
		obj.put("usage", usage);
		obj.put("owner", ownerOnly);
		return obj;
	}

	public JSONArray getAsJsonArray() {
		JSONArray array = new JSONArray();
		for (Command c : commands) {
			JSONObject obj = new JSONObject();
			obj.put("name", c.getClass().getSimpleName());
			obj.put("data", jsonfy(c));
			array.put(obj);
		}
		return array;
	}

	public File generateJson(String path) {
		JSONArray array = getAsJsonArray();
		File f = new File(path);
		try {
			Files.write(Paths.get(path), array.toString(4).getBytes());
			logger.logError(new Exception("Generated Commands as Json: " + path));
		} catch (JSONException e) {
			logger.logError(e);
		} catch (IOException e) {
			logger.logError(e);
		}
		return f;
	}

}
