package minn.minnbot.entities.command;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import org.json.JSONObject;

import java.net.URLEncoder;

public class UrbanCommand extends CommandAdapter {

    public UrbanCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        HttpResponse<JsonNode> response;
        try {
            //noinspection deprecation
            response = Unirest.get("https://mashape-community-urban-dictionary.p.mashape.com/define?term=" + URLEncoder.encode(event.allArguments.trim()))
                    .header("X-Mashape-Key", "IlX3p3hnDRmsheyTT7z87aT1mrs9p1Qb4WkjsnGUnXKitYqhtf")
                    .header("Accept", "text/plain")
                    .asJson();
            JSONObject body = response.getBody().getObject();
            if (body.has("list")) {
                String url = body.getJSONArray("list").getJSONObject(0).getString("permalink");
                String definition = body.getJSONArray("list").getJSONObject(0).getString("definition");
                String msg = url + " - *" + definition.trim().replace("*", "") + "*";
                if (msg.length() <= 1500)
                    event.sendMessage(msg);
                else
                    event.sendMessage(url);
            } else {
                event.sendMessage("Something went wrong with your request!");
            }
        } catch (UnirestException e) {
            logger.logThrowable(e);
            event.sendMessage("Something went wrong with your request!");
        }
    }

    @Override
    public String getAlias() {
        return "urban <query>";
    }

    public String example() {
        return "urban wat";
    }
}
