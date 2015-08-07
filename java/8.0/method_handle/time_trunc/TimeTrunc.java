import static java.lang.invoke.MethodHandles.*;
import static java.lang.invoke.MethodType.*;

import java.lang.invoke.MethodHandle;

public class TimeTrunc {
	public static void main(String... args) throws Throwable {
		long now = System.currentTimeMillis();

		System.out.println("Date: " + trunc(new java.util.Date(now)));
		System.out.println("sql.Date: " + trunc(new java.sql.Date(now)));
		System.out.println("timestamp" + trunc(new java.sql.Timestamp(now)));
	}

	private static <D extends java.util.Date> D trunc(D date) throws Throwable {
		System.out.println("class: " + date.getClass());

		long time = date.getTime();

		MethodHandle mh = publicLookup().findConstructor(date.getClass(), methodType(void.class, long.class));

		// invokeExact ‚ÍŽg‚¦‚È‚¢
		return (D)mh.invoke(hourTrunc(time));
	}

	private static long hourTrunc(long time) {
		long hour = 60 * 60 * 1000;
		return (time / hour) * hour;
	}
}
