import java.text.SimpleDateFormat;
import java.util.Date;

public class ParseIsoDate {
	public static void main(String... args) throws Exception {
		System.out.println(format("yyyy-MM-dd'T'HH:mm:ssX", args[0]));
	}

	private static Date format(String format, String dateString) throws Exception {
		return new SimpleDateFormat(format).parse(dateString);
	}
}
