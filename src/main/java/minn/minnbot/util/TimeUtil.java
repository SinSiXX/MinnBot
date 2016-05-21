package minn.minnbot.util;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static String timeStamp() {
        java.time.LocalTime time = java.time.LocalTime.now(java.time.Clock.systemDefaultZone());
        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();
        return "[" + ((hour < 10) ? "0" + hour : hour) + ":" + ((minute < 10) ? "0" + minute : minute) + ":"
                + ((second < 10) ? "0" + second : second) + "]";
    }

    public static String getJoinDate(User u, Guild g) {
        if (g == null)
            return "NaN";
        java.time.OffsetDateTime time = g.getJoinDateForUser(u);
        int day = time.getDayOfMonth();
        int month = time.getMonthValue();
        int year = time.getYear();

        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();
        long milli = time.toInstant().toEpochMilli();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(time.toInstant().toEpochMilli());
        /*return "" + ((day < 10) ? "0" + day : day) + "-" + ((month < 10) ? "0" + month : month) + "-" + year + " | "
                + ((hour < 10) ? "0" + hour : hour) + ":" + ((minute < 10) ? "0" + minute : minute) + ":"
                + ((second < 10) ? "0" + second : second);*/
    }

    public static String uptime(long inMillis) {

        List<String> times = new LinkedList<>();

        long days = TimeUnit.MILLISECONDS.toDays(inMillis);
        inMillis -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(inMillis);
        inMillis -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(inMillis);
        inMillis -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(inMillis);
        inMillis -= TimeUnit.SECONDS.toMillis(seconds);

        if (days > 0) {
            times.add(days + " day" + (days != 1 ? "s" : ""));
        }
        if (hours > 0) {
            times.add(hours + " hour" + (hours != 1 ? "s" : ""));
        }
        if (minutes > 0) {
            times.add(minutes + " minute" + (minutes != 1 ? "s" : ""));
        }
        if (seconds > 0) {
            times.add(seconds + " second" + (seconds != 1 ? "s" : ""));
        }

        String uptime = "";

        for (int i = 0; i < times.size() - 1; i++) {
            uptime += times.get(i) + ", ";
        }

        if (times.size() != 1 && uptime.length() > 2)
            return uptime.substring(0, uptime.length() - 2) + " and " + times.get(times.size() - 1);
        else
            return times.get(0);
    }

    /**
     * @param id of object to check creation time from
     * @return Creation time in SimpleDateFormat
     */
    public static String getCreationTime(long id) {
        long time = ((id >> 22) + 1420070400000L);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        return sdf.format(time);
    }

}
