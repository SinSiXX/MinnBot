package minn.minnbot.util;

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
	
}
