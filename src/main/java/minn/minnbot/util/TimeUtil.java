package minn.minnbot.util;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class TimeUtil {

	public static String timeStamp() {
		java.time.LocalTime time = java.time.LocalTime.now(java.time.Clock.systemDefaultZone());
		int hour = time.getHour();
		int minute = time.getMinute();
		int second = time.getSecond();
		String stamp = "[" + ((hour < 10) ? "0" + hour : hour) + ":" + ((minute < 10) ? "0" + minute : minute) + ":"
				+ ((second < 10) ? "0" + second : second) + "]";
		return stamp;
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
