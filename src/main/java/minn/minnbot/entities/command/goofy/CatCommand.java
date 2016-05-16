package minn.minnbot.entities.command.goofy;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;

public class CatCommand extends CommandAdapter {

    public CatCommand(String prefix, Logger logger) {
        super.init(prefix, logger);
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
    public String getAlias() {
        return "cat";
    }

    @Override
    public String example() {
        return "cat";
    }
}
