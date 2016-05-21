package minn.minnbot.util;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.UnexpectedException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TwitchUtil {

    public static Stream getStream(String name) throws UnexpectedException, UnirestException {
        return new Stream(name);
    }

    public static Channel getChannel(String name) throws UnexpectedException, UnirestException {
        return new Channel(name);
    }

    public static class Emote implements Comparable<Emote> {

       /* private static Future<HttpResponse<String>> subs = Unirest.get("https://twitchemotes.com/api_cache/v2/subscriber.json").asStringAsync();
        private static Future<HttpResponse<String>> global = Unirest.get("https://twitchemotes.com/api_cache/v2/global.json").asStringAsync();*/

        private static List<Emote> allEmotes = null;
        private static List<Emote> subEmotes = null;
        private static List<Emote> globalEmotes = null;

        private static JSONObject object = null;

        private static void refresh() {
            allEmotes = new LinkedList<>();
            subEmotes = new LinkedList<>();
            globalEmotes = new LinkedList<>();

            globalEmotes.addAll(getGlobal());
            subEmotes.addAll(getSub());

            allEmotes.addAll(globalEmotes);
            allEmotes.addAll(subEmotes);
        }

        private static List<Emote> getGlobal() {
            String template = "";
            try {
                JSONObject tmp = new JSONObject(Unirest.get("https://twitchemotes.com/api_cache/v2/global.json").asString().getBody());
                template = tmp.getJSONObject("template").getString("large");
                object = tmp.getJSONObject("emotes");
            } catch (NullPointerException ignored) {
                ignored.printStackTrace();
                return new LinkedList<>();
            } catch (JSONException e) {
                try {
                    System.out.print(new JSONObject(Unirest.get("https://twitchemotes.com/api_cache/v2/global.json").asString().getBody()).toString(4));
                } catch (UnirestException e1) {
                    e1.printStackTrace();
                }
                return new LinkedList<>();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            List<Emote> list = new LinkedList<>();
            for (Iterator<String> it = object.keys(); it.hasNext(); ) {
                try {
                    String key = it.next();
                    list.add(new Emote(key, object.getJSONObject(key).getLong("image_id"), template));
                } catch (JSONException | ClassCastException ignored) {
                }
            }
            return list;
        }

        private static List<Emote> getSub() {
            String template = "";
            try {
                JSONObject tmp = new JSONObject(Unirest.get("https://twitchemotes.com/api_cache/v2/subscriber.json").asString().getBody());
                template = tmp.getJSONObject("template").getString("large");
                object = tmp.getJSONObject("channels");
            } catch (NullPointerException ignored) {
                return new LinkedList<>();
            } catch (JSONException e) {
                return new LinkedList<>();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            List<Emote> list = new LinkedList<>();
            for (Iterator<String> it = object.keys(); it.hasNext(); ) {
                try {
                    JSONObject channel = object.getJSONObject(it.next());
                    JSONArray arr = channel.getJSONArray("emotes");
                    for (Object anArr : arr) {
                        JSONObject emote = (JSONObject) anArr;
                        list.add(new Emote(emote.getString("code"), emote.getLong("image_id"), template));
                    }
                } catch (JSONException | ClassCastException ignored) {
                }
            }
            return list;
        }

        static boolean isAvailable() {
            if (allEmotes == null || allEmotes.isEmpty()) {
                refresh();
                return allEmotes != null && !allEmotes.isEmpty();
            }
            return true;
        }

        public static Emote getByCode(String code) throws UnexpectedException {
            if (!isAvailable())
                throw new UnexpectedException("EmoteList is not available!");
            return allEmotes.stream().filter(e -> e.CODE.equalsIgnoreCase(code)).findFirst().orElse(null);
        }

        public static Emote findFirstContaining(String code) throws UnexpectedException {
            if (!isAvailable())
                throw new UnexpectedException("EmoteList is not available!");
            code = code.toLowerCase();

            if (code.matches("((?i)sub\\s+.+|.+\\s+(?i)sub)")) {// ONLY LOOK FOR SUB EMOTES
                code = code.replaceAll("((?i)sub\\s+|\\s+(?i)sub)", "");
                String finalCode = code;
                return subEmotes.stream().filter(e -> e.CODE.toLowerCase().contains(finalCode)).sorted().findFirst().orElse(null);
            } else if (code.matches("((?i)global\\s+.+|.+\\s+(?i)global)")) {// ONLY LOOK FOR GLOBAL EMOTES
                code = code.replaceAll("((?i)global\\s+|\\s+(?i)global)", "");
                String finalCode = code;
                return globalEmotes.stream().filter(e -> e.CODE.toLowerCase().contains(finalCode)).sorted().findFirst().orElse(null);
            }
            String finalCode = code;
            return allEmotes.stream().filter(e -> e.CODE.toLowerCase().contains(finalCode)).sorted().findFirst().orElse(null);
        }

        public static List<Emote> getEmotesByCode(String code) throws UnexpectedException {
            if (!isAvailable())
                throw new UnexpectedException("EmoteList is not available!");
            code = code.toLowerCase();
            String finalCode = code;
            return allEmotes.stream().filter(e -> e.CODE.contains(finalCode)).collect(Collectors.toList());
        }

        public static Emote getById(long id) throws UnexpectedException {
            if (!isAvailable())
                throw new UnexpectedException("EmoteList is not available!");
            return allEmotes.stream().filter(e -> e.ID == id).findFirst().orElse(null);
        }

        public static Emote findFirstContaining(long id) throws UnexpectedException {
            if (!isAvailable())
                throw new UnexpectedException("EmoteList is not available!");
            return allEmotes.stream().filter(e -> ("" + e.ID).contains("" + id)).sorted().findFirst().orElse(null);
        }

        public static List<Emote> getEmotesById(long id) throws UnexpectedException {
            if (!isAvailable())
                throw new UnexpectedException("EmoteList is not available!");
            return allEmotes.stream().filter(e -> ("" + e.ID).contains("" + id)).collect(Collectors.toList());
        }

        public final long ID;
        public final String CODE;
        private String template;

        Emote(String code, long id, String template) {
            this.CODE = code;
            this.ID = id;
            this.template = template;
        }

        public String toString() {
            return new JSONObject().put("id", ID).put("code", CODE).put("template", template).toString(4);
        }

        public BufferedImage download() throws IOException, UnirestException {
            try {
                return ImageIO.read(new URL(template.replace("{image_id}", "" + ID)));
            } catch (IOException e) {
                refresh();
                return null;
            }
        }

        public boolean download(File file) throws IOException, UnirestException {
            BufferedImage img = download();
            if(img == null)
                return false;
            return ImageIO.write(img, "png", file);
        }

        @Override
        public int compareTo(Emote other) {
            return this.CODE.length() - other.CODE.length();
        }

    }

    public static class Stream {
        private JSONObject obj;
        private JSONObject stream;
        private Channel channel;

        Stream(String name) throws UnirestException, UnexpectedException {
            //noinspection deprecation
            obj = Unirest.get("https://api.twitch.tv/kraken/streams/" + URLEncoder.encode(name.toLowerCase())).header("accept", "application/vnd.twitchtv.v3+json").asJson().getBody().getObject();
            if (!obj.has("stream") || !(obj.get("stream") instanceof JSONObject)) {
                throw new UnexpectedException("Stream is not available! - " + name);
            }
            stream = obj.getJSONObject("stream");
            if (!stream.has("channel") || !(stream.get("channel") instanceof JSONObject)) {
                throw new UnexpectedException("Channel is not available! - " + name);
            }
            channel = new Channel(stream.getJSONObject("channel"));
        }

        public OffsetDateTime getCreationTime() {
            return stream.get("created_at") instanceof String ? OffsetDateTime.parse(stream.getString("created_at")) : null;
        }

        public long getUptime() {
            OffsetDateTime time = getCreationTime();
            if (time == null)
                return 0;
            return Duration.between(time.atZoneSameInstant(TimeZone.getTimeZone("GMT").toZoneId()), OffsetDateTime.now().atZoneSameInstant(TimeZone.getTimeZone("GMT").toZoneId())).toMillis();
        }

        public JSONObject getObject() {
            return stream == null ? new JSONObject() : stream;
        }

        public boolean isPlaylist() {
            Object obj = stream.get("is_playlist");
            return obj != null && (boolean) obj;
        }

        public float getFPS() {
            Object obj = stream.get("average_fps");
            return obj == null ? 0 : new Float(obj.toString());
        }

        public List<String> getPreviews() {
            Object obj = stream.get("preview");
            if (obj == null || !(obj instanceof JSONObject))
                return new LinkedList<>();
            JSONObject jsonObject = (JSONObject) obj;
            List<String> list = new LinkedList<>();
            list.add(jsonObject.getString("small"));
            list.add(jsonObject.getString("medium"));
            list.add(jsonObject.getString("large"));
            list.add(jsonObject.getString("template"));
            return list;
        }

        public int getViewers() {
            return stream.getInt("viewers");
        }

        public String getURL() {
            return channel.getURL();
        }

        public String getPreview(PreviewType type) {
            return type == null ? "" : stream.getJSONObject("preview").getString(type.name().toLowerCase());
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
            if (object == null)
                throw new NullPointerException("Channel is null!");
            if (!object.has("_id"))
                throw new IllegalArgumentException("Channel JSON is malformed: \n" + object.toString(4));
            this.object = object;
        }

        public JSONObject getObject() {
            return object == null ? new JSONObject() : object;
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

    public static void main(String... a) throws IOException, UnirestException {
        PrintStream out = System.out;
        Scanner sc = new Scanner(System.in);
        Emote.refresh();
        File f = new File("testEmote.png");
        f.createNewFile();
        f.deleteOnExit();
        out.print(Emote.getByCode("fmgTHUMP").download(f));
    }

}
