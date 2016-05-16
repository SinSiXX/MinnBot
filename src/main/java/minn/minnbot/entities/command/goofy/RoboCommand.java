package minn.minnbot.entities.command.goofy;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import org.json.JSONObject;

import java.net.URLEncoder;

public class RoboCommand extends CommandAdapter {

    public RoboCommand(String prefix, Logger logger) {
        init(prefix, logger);
    }

    @Override
    public void onCommand(CommandEvent event) {
        try {
            //noinspection deprecation
            JSONObject response = Unirest.get(String.format("https://robohash.p.mashape.com/index.php?text=%s", URLEncoder.encode(event.allArguments)))
                    .header("X-Mashape-Key", "IlX3p3hnDRmsheyTT7z87aT1mrs9p1Qb4WkjsnGUnXKitYqhtf")
                    .header("Accept", "application/json")
                    .asJson().getBody().getObject();
            if(response.has("imageUrl")) {
                event.sendMessage(response.getString("imageUrl"));
            } else {
                event.sendMessage(String.format("```json%s```", response.toString(4)));
            }

        } catch (UnirestException e) {
            event.sendMessage("Something went wrong with my connection.");
        }
    }

    @Override
    public String getAlias() {
        return "robo [<text>]";
    }

    public String example() {
        return "robo bot";
    }
}
