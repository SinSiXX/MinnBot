package minn.minnbot.entities.command;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.command.listener.CommandAdapter;
import minn.minnbot.events.CommandEvent;
import minn.minnbot.util.EmoteUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class GifCommand extends CommandAdapter {

    public GifCommand(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onCommand(CommandEvent event) {
        try {
            String term = event.allArguments.replace(" ", "+");
            String url = "http://api.giphy.com/v1/gifs/random?api_key=dc6zaTOxFJmzC&rating=pg-13&tag=" + URLEncoder.encode(term).replace("%2B", "+");
            if (!term.isEmpty()) {
                term = term.replace(" ", "+");
                url = "http://api.giphy.com/v1/gifs/random?api_key=dc6zaTOxFJmzC&rating=pg-13&tag=" + URLEncoder.encode(term).replace("%2B", "+");
            }
            System.out.println(url);
            com.mashape.unirest.http.HttpResponse<JsonNode> jsonResponse = Unirest.get(url)
                    .header("accept", "application/json").asJson();
            System.out.println(jsonResponse.getBody().toString());
            JSONObject obj = new JSONObject(jsonResponse.getBody().toString());
            if(obj.getJSONObject("meta").getInt("status") == 404) {
                event.sendMessage("Forbidden.");
                return;
            } else if(obj.getJSONObject("meta").getInt("status") != 200) {
                event.sendMessage("Something went wrong with your search request.");
                return;
            }
            try {
                url = obj.getJSONObject("data").getString("image_original_url");
                event.sendMessage(url);
            } catch (JSONException ignored) {
                event.sendMessage("Nothing to see here.");
            }
        } catch (Exception e) {
            if (e instanceof UnirestException) {
                event.sendMessage("Unsupported characters in `" + event.allArguments + "`. " + EmoteUtil.getRngThumbsdown());
                return;
            }
            logger.logThrowable(e);
        }
    }

    @Override
    public boolean isCommand(String message) {
        String[] parts = message.split(" ", 2);
        return parts.length >= 1 && parts[0].equalsIgnoreCase(prefix + "gif");
    }

    @Override
    public String getAlias() {
        return "gif";
    }
}
