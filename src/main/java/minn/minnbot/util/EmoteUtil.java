package minn.minnbot.util;

public class EmoteUtil {

    public static String getRngThumbsdowns() {
        return ":thumbsdown::skin-tone-" + ((int)Math.floor(Math.random() * 5)) + ":";
    }

    public static String getRngThumbsup() {
        return ":thumbsup::skin-tone-" + ((int)Math.floor(Math.random() * 5)) + ":";
    }

    public static String getRngOkHand() {
        return ":ok_hand::skin-tone-" + ((int)Math.floor(Math.random() * 5)) + ":";
    }

}
