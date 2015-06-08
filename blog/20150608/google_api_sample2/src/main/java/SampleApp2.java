import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileInputStream;
import java.util.Arrays;

public class SampleApp2 {

	private static final String SPREADSHEETS_FEED_BASE = "https://spreadsheets.google.com/feeds/";
	private static final String SPREADSHEETS_FEED_FULL =
			SPREADSHEETS_FEED_BASE + "spreadsheets/private/full";

	public static void main(String... args) throws Exception {
		FileInputStream jsonKeyFile = new FileInputStream(args[0]);

		GoogleCredential credential = GoogleCredential.fromStream(jsonKeyFile)
				.createScoped(Arrays.asList(SPREADSHEETS_FEED_BASE));

		credential.refreshToken();

		HttpClient client = HttpClientBuilder.create().build();

		HttpGet get = new HttpGet(SPREADSHEETS_FEED_FULL);
		get.addHeader("Authorization", "Bearer " + credential.getAccessToken());

		HttpResponse res = client.execute(get);

		res.getEntity().writeTo(System.out);
		System.out.println("");
	}
}
