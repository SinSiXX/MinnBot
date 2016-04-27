package minn.minnbot.util;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Vector;

public class TimeUtil {

	public static String timeStamp() {
		java.time.LocalTime time = java.time.LocalTime.now(java.time.Clock.systemDefaultZone());
		int hour = time.getHour();
		int minute = time.getMinute();
		int second = time.getSecond();
		return "[" + ((hour < 10) ? "0" + hour : hour) + ":" + ((minute < 10) ? "0" + minute : minute) + ":"
				+ ((second < 10) ? "0" + second : second) + "]";
	}

	public static String uptime(int inMillis) {
		long nHours = 0L;
		long nMinutes = 0L;
		long nSeconds = 0L;
		long nDays = 0L;
		String[] times = new String[4];
		String[] timeDataPlural = { " Days", " Hours", " Minutes", " Seconds" };
		String[] timeDataSingular = { " Day", " Hour", " Minute", " Second" };
		nSeconds = (int) (inMillis / 1000L) % 60;
		nMinutes = (int) (inMillis / 60000L % 60L);
		nHours = (int) (inMillis / 3600000L % 24L);
		nDays = (int) (inMillis / 86400000L);
		times[3] = "" + nSeconds;
		times[2] = "" + nMinutes;
		times[1] = "" + nHours;
		times[0] = "" + nDays;
		int[] numbers = { Integer.parseInt(times[0]), Integer.parseInt(times[1]), Integer.parseInt(times[2]),
				Integer.parseInt(times[3]) };
		Vector<String> list = new Vector<String>();
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
		return time;
	}

	/**
	 * Thanks dinos
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
