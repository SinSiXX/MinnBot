package minn.minnbot.entities.command.goofy;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import org.json.JSONArray;
import org.json.JSONObject;

public class FactCommand extends CommandAdapter {

    public FactCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        try {
            JSONObject obj = Unirest.get("http://catfacts-api.appspot.com/api/facts").asJson().getBody().getObject();
            if(!obj.has("facts") || !obj.getBoolean("success")) {
                event.sendMessage("No facts available!");
                return;
            }
            JSONArray arr = obj.getJSONArray("facts");
            event.sendMessage(String.format("**%s**", arr.get(0)));
        } catch (UnirestException e) {
            event.sendMessage("Something went wrong. :pensive:");
        }
    }

    @Override
    public String getAlias() {
        return "catFact";
    }
}
