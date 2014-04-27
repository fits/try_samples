
import java.text.SimpleDateFormat;
import java.util.Date;

public class Sample1 {
	public static void main(String... args) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

		Date date = df.parse(args[0]);

		System.out.println(date);
	}
}