package minn.minnbot.entities.command.goofy;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import org.json.JSONObject;

import java.util.Random;

public class JokeCommand extends CommandAdapter {

    public JokeCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        try {
            JSONObject obj;
            if(new Random().nextInt(2) == 1) {
                JSONObject object = Unirest.get("http://api.icndb.com/jokes/random").header("accept", "application/json").asJson().getBody().getObject();
                if (!object.getString("type").equals("success")) {
                    event.sendMessage("Your face!");
                    return;
                }
                obj = object.getJSONObject("value");
            } else {
                JSONObject object = Unirest.get("http://api.yomomma.info/").header("accept", "application/json").asJson().getBody().getObject();
                if(!object.has("joke")) {
                    event.sendMessage("Your face!");
                    return;
                }
                obj = object;
            }
            event.sendMessage(obj.getString("joke"));
        } catch (UnirestException e) {
            event.sendMessage("Your face!");
        }
    }

    @Override
    public String getAlias() {
        return "joke";
    }

}
