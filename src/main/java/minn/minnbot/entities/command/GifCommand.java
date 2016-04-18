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

    private String key;

    public GifCommand(String prefix, Logger logger, String key) throws UnirestException {
        this.prefix = prefix;
        this.logger = logger;
        this.key = key;

        String url = "http://api.giphy.com/v1/gifs/random?api_key=" + key + "&rating=pg-13&tag=cat";
        com.mashape.unirest.http.HttpResponse<JsonNode> jsonResponse = Unirest.get(url)
                .header("accept", "application/json").asJson();
        if (jsonResponse.getBody().getObject().getJSONObject("meta").getInt("status") > 299) {
            logger.logThrowable(new IllegalArgumentException("Giphy key is invalid"));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onCommand(CommandEvent event) {
        try {
            // get request for giphy
            String term = event.allArguments;
            String url = "http://api.giphy.com/v1/gifs/random?api_key=" + key + "&rating=pg-13&tag=" + URLEncoder.encode(term);
            if (!term.isEmpty()) {
                term = term.replace(" ", "+");
                url = "http://api.giphy.com/v1/gifs/random?api_key=" + key + "&rating=pg-13&tag=" + URLEncoder.encode(term);
            }
            com.mashape.unirest.http.HttpResponse<JsonNode> jsonResponse = Unirest.get(url)
                    .header("accept", "application/json").asJson();
            JSONObject obj = new JSONObject(jsonResponse.getBody().toString());
            if (obj.getJSONObject("meta").getInt("status") == 404) {
                event.sendMessage("Forbidden.");
                return;
            } else if (obj.getJSONObject("meta").getInt("status") != 200) {
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
        return "gif <query>";
    }

    public String usage() {
        return "Either search for tags: `gif cute cat` to get a random gif with fitting tags.\n" +
                " Or just type `gif` to see something completely random from the giphy database.";
    }
}
