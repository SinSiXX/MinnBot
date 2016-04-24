package minn.minnbot.util;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class MemeUtil {

    @SuppressWarnings("deprecated")
    public static String generateMeme(String template, String firstText, String secondText) throws UnirestException {
        try {
            JSONObject obj = Unirest.get("http://memegen.link/" + template + "/" + URLEncoder.encode(firstText) + "/" + URLEncoder.encode(secondText)).asJson().getBody().getObject();
            return obj.getJSONObject("direct").getString("visible");
        } catch(JSONException ignored) {
            return "Usage: `meme <template> <text> | <text>` Templates: http://memegen.link/templates";
        } catch (Exception ignored) {
            return "`" + ignored.getMessage() + "`";
        }
    }

}
