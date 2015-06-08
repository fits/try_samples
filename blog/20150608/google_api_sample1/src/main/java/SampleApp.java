import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import java.io.FileInputStream;
import java.util.Arrays;

public class SampleApp {
	public static void main(String... args) throws Exception {
		FileInputStream jsonKeyFile = new FileInputStream(args[0]);

		GoogleCredential credential = GoogleCredential.fromStream(jsonKeyFile)
				.createScoped(Arrays.asList("https://spreadsheets.google.com/feeds/"));

		credential.refreshToken();

		System.out.println("access token: " + credential.getAccessToken());
	}
}
