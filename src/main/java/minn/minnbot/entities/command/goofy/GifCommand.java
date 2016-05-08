package minn.minnbot.entities.command.goofy;

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
import java.util.List;

public class GifCommand extends CommandAdapter {

    private String key;

    public GifCommand(String prefix, Logger logger, String key) throws UnirestException {
        init(prefix, logger);
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
    public boolean isCommand(String message, List<String> prefixList) {
        String[] p = message.split(" ", 2);
        if(p.length < 1)
            return false;
        if(p[0].equalsIgnoreCase(prefix + "gif"))
            return true;
        for(String fix : prefixList) {
            if(p[0].equalsIgnoreCase(fix + "gif"))
                return true;
        }
        return false;
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
