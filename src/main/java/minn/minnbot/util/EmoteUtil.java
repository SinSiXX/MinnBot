package minn.minnbot.util;

import java.util.Random;

public class EmoteUtil {

    public static String getRngThumbsdowns() {
        return ":thumbsdown::skin-tone-" + (new Random().nextInt(4) + 1) + ":";
    }

    public static String getRngThumbsup() {
        return ":thumbsup::skin-tone-" + (new Random().nextInt(4) + 1) + ":";
    }

    public static String getRngOkHand() {
        return ":ok_hand::skin-tone-" + (new Random().nextInt(4) + 1)  + ":";
    }

}
