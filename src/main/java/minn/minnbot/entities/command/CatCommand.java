package minn.minnbot.entities.command;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

public class CatCommand extends CommandAdapter {

    public CatCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    @Override
    public void onCommand(CommandEvent event) {
        try {
            JsonNode obj = Unirest.get("http://random.cat/meow").asJson().getBody();
            String url = obj.getObject().getString("file");
            event.sendMessage(url);
        } catch (Exception e) {
            logger.logThrowable(e);
            event.sendMessage("Something went wrong. Contact the dev please. " + e.getMessage());
        }
    }

    @Override
    public boolean isCommand(String message) {
        try {
            String cmd = message.split(" ", 2)[0];
            return cmd.equalsIgnoreCase(prefix + "cat");
        } catch (IndexOutOfBoundsException ignore) {}
        return false;
    }

    @Override
    public String getAlias() {
        return "cat";
    }
}
