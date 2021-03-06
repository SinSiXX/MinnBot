package minn.minnbot.util;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.rmi.UnexpectedException;

public class TwitchUtil {

    public static Stream getStream(String name) throws UnexpectedException, UnirestException {
        return new Stream(name);
    }

    public static Channel getChannel(String name) throws UnexpectedException, UnirestException {
        return new Channel(name);
    }

    public static class Stream {
        private JSONObject obj;
        private JSONObject stream;
        private Channel channel;

        Stream(String name) throws UnirestException, UnexpectedException {
            //noinspection deprecation
            obj = Unirest.get("https://api.twitch.tv/kraken/streams/" + URLEncoder.encode(name.toLowerCase())).header("accept", "application/vnd.twitchtv.v3+json").asJson().getBody().getObject();
            if (!obj.has("stream") || !(obj.get("stream") instanceof JSONObject)) {
                throw new UnexpectedException("Stream is not available!");
            }
            stream = obj.getJSONObject("stream");
            if (!stream.has("channel") || !(stream.get("channel") instanceof JSONObject)) {
                throw new UnexpectedException("Channel is not available!");
            }
            channel = new Channel(stream.getJSONObject("channel"));
        }

        public int getViewers() {
            return stream.getInt("viewers");
        }

        public String getURL() {
            return channel.getURL();
        }

        public String getPreview(PreviewType type) {
            if (type == null)
                return "";
            return stream.getJSONObject("preview").getString(type.name().toLowerCase());
        }

        public String getGame() {
            Object game = stream.get("game");
            return game instanceof String ? (String) game : "";
        }

        public Channel getChannel() {
            return channel;
        }

        public enum PreviewType {SMALL, MEDIUM, LARGE, TEMPLATE}

    }

    public static class Channel {

        private JSONObject object;

        Channel(String name) throws UnirestException, UnexpectedException {
            //noinspection deprecation
            object = Unirest.get("https://api.twitch.tv/kraken/channels/" + URLEncoder.encode(name.toLowerCase())).header("accept", "application/vnd.twitchtv.v3+json").asJson().getBody().getObject();
            if (!object.has("_id"))
                throw new UnexpectedException("Channel is not available.");
        }

        Channel(JSONObject object) {
            if(object == null)
                throw new NullPointerException("Channel is null!");
            if (!object.has("_id"))
                throw new IllegalArgumentException("Channel JSON is malformed: \n" + object.toString(4));
            this.object = object;
        }

        public String getURL() {
            Object url = object.get("url");
            return url instanceof String ? (String) url : "undefined";
        }

        public boolean isPartnered() {
            Object partnered = object.get("partner");
            return partnered instanceof Boolean && (boolean) partnered;
        }

        public boolean isMature() {
            Object mature = object.get("mature");
            return mature instanceof Boolean && (boolean) mature;
        }

        public String getGame() {
            Object game = object.get("game");
            return game instanceof String ? (String) game : "";
        }

        public String getName() {
            Object name = object.get("name");
            return name instanceof String ? (String) name : "undefined";
        }

        public String getStatus() {
            Object status = object.get("status");
            return status instanceof String ? (String) status : "undefined";
        }

    }

}
