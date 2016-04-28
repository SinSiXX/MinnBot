package minn.minnbot.util;

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

        if (times.size() != 1)
            return uptime.substring(0, uptime.length() - 2) + " and " + times.get(times.size() - 1);
        else
            return times.get(0);
        /*AtomicLong nHours = new AtomicLong();
        AtomicLong nMinutes = new AtomicLong();
		AtomicLong nSeconds = new AtomicLong();
		AtomicLong nDays = new AtomicLong();
		String[] times = new String[4];
		String[] timeDataPlural = { " Days", " Hours", " Minutes", " Seconds" };
		String[] timeDataSingular = { " Day", " Hour", " Minute", " Second" };
		nSeconds.set((int) (inMillis / 1000L) % 60);
		nMinutes.set((int) (inMillis / 60000L % 60L));
		nHours.set((int) (inMillis / 3600000L % 24L));
		nDays.set((int) (inMillis / 86400000L));
		times[3] = "" + nSeconds.get();
		times[2] = "" + nMinutes.get();
		times[1] = "" + nHours.get();
		times[0] = "" + nDays.get();
		int[] numbers = { Integer.parseInt(times[0]), Integer.parseInt(times[1]), Integer.parseInt(times[2]),
				Integer.parseInt(times[3]) };
		List<String> list = new LinkedList<>();
		for (int i = 0; i < 4; i++) {
			if (numbers[i] > 0) {
				if (numbers[i] > 1) {
					list.add(numbers[i] + timeDataPlural[i]);
				} else {
					list.add(numbers[i] + timeDataSingular[i]);
				}
			}
		}
		String time = "";
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				time = time + list.get(i);
			} else if (i == list.size() - 1) {
				time = time + " and " + list.get(i);
			} else if (i > 0) {
				time = time + ", " + list.get(i);
			}
		}
		return time;*/
    }

    /**
     * Thanks dinos
     *
     * @param id of object to check creation time from
     * @return Creation time in SimpleDateFormat
     */
    public static String getCreationTime(long id) {
        long time = ((id >> 22) + 1420070400000L);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        return sdf.format(time);
    }

}
