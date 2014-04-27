
import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;

public class Sample2 {
	public static void main(String... args) throws Exception {
		Date date = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(args[0]);

		System.out.println(date);
	}
}