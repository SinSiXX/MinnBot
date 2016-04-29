package minn.minnbot.entities.command.goofy;

import java.net.URLEncoder;

import minn.minnbot.entities.Logger;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;

public class GoogleCommand extends CommandAdapter {
	public GoogleCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    @Override
    @SuppressWarnings( "deprecation" )
    public void onCommand(CommandEvent event) {
	if(event.allArguments.isEmpty()) {
  		event.sendMessage("Missing arguments. Usage: " + usage());
  		return;
	}
		event.sendMessage("http://lmgtfy.com/?q=" + URLEncoder.encode(event.allArguments));
	}

    public boolean isCommand(String message) {
    	String[] p = message.split(" ",2);
        return p.length > 0 && p[0].equalsIgnoreCase(prefix + "lmgtfy");
      }

    public String usage() {
        return "lmgtfy <query> | Returns a let me google that for you link with the added query";
    }

    @Override
    public String getAlias() {
        return "lmgtfy <query>";
    }
}
