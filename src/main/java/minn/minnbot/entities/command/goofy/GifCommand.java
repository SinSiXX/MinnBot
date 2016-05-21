package minn.minnbot.entities.command.goofy;

import com.mashape.unirest.http.HttpResponse;
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
    private boolean available = true;

    public GifCommand(String prefix, Logger logger, String key) throws UnirestException {
        init(prefix, logger);
        this.key = key;
        String url = String.format("http://api.giphy.com/v1/gifs/random?api_key=%s&rating=pg-13&tag=cat", key);
        JSONObject response = Unirest.get(url).asJson().getBody().getObject();
        if(response.getJSONObject("meta").getInt("status") > 299) {
            logger.logInfo("Giphy key is invalid.");
            logger.logInfo(response.toString(4));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onCommand(CommandEvent event) {
        try {
            String term = event.allArguments;
            String url = String.format("http://api.giphy.com/v1/gifs/random?api_key=%s&rating=pg-13&tag=%s", key, URLEncoder.encode(term));
            HttpResponse<JsonNode> jsonResponse = Unirest.get(url)
                    .header("accept", "application/json").asJson();
            JSONObject obj = new JSONObject(jsonResponse.getBody().toString());
            int status = obj.getJSONObject("meta").getInt("status");
            if (obj.getJSONObject("meta").getInt("status") == 404) {
                event.sendMessage("Forbidden.");
                return;
            } else if (obj.getJSONObject("meta").getInt("status") >= 300) {
                event.sendMessage("Something went wrong with your search request. Status " + obj.getJSONObject("meta").getInt("status") );
                return;
            }
            try {
                url = obj.getJSONObject("data").getString("image_original_url");
                event.sendMessage(url);
            } catch (JSONException e) {
                event.sendMessage("Nothing to see here.");
            }
        } catch (Exception e) {
            if (e instanceof UnirestException) {
                event.sendMessage("Unsupported characters in `" + event.allArguments + "`. " + EmoteUtil.getRngThumbsdown());
                return;
            }
            if(e instanceof JSONException) {
                event.sendMessage("This command is not available in the current session!");
                return;
            }
            logger.logThrowable(e);
        }
    }

    @Override
    public String getAlias() {
        return "gif <query>";
    }

    public String usage() {
        return "Either search for tags: `gif cute cat` to get a random gif with fitting tags.\n" +
                " Or just type `gif` to see something completely random from the giphy database.";
    }

    @Override
    public String example() {
        return "gif cats";
    }

}
