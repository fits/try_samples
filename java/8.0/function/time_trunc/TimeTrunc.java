import static java.lang.invoke.MethodHandles.*;
import static java.lang.invoke.MethodType.*;

import java.lang.invoke.MethodHandle;

public class TimeTrunc {
	public static void main(String... args) throws Throwable {
		long now = System.currentTimeMillis();

		System.out.println(now);

		System.out.println("Date: " + trunc(new java.util.Date(now)));
		System.out.println("sql.Date: " + trunc(new java.sql.Date(now)));
		System.out.println("timestamp" + trunc(new java.sql.Timestamp(now)));
	}

	private static java.util.Date trunc(java.util.Date date) {
		return trunc(date.getTime(), java.util.Date::new);
	}

	private static java.sql.Date trunc(java.sql.Date date) {
		return trunc(date.getTime(), java.sql.Date::new);
	}

	private static java.sql.Timestamp trunc(java.sql.Timestamp date) {
		return trunc(date.getTime(), java.sql.Timestamp::new);
	}

	private static <D> D trunc(long date, java.util.function.LongFunction<D> ct) {		return ct.apply(hourTrunc(date));
	}

	private static long hourTrunc(long time) {
		long hour = 60 * 60 * 1000;
		return (time / hour) * hour;
	}
}
