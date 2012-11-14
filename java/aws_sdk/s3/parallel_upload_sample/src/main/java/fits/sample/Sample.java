package fits.sample;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.Region;
import static com.amazonaws.services.s3.model.CannedAccessControlList.*;

public class Sample {
	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.load(Sample.class.getResourceAsStream("setting.properties"));

		final AmazonS3Client client = new AmazonS3Client(new BasicAWSCredentials(
			props.getProperty("s3_accessKey"),
			props.getProperty("s3_secretKey")
		));
		/**
		 * 東京リージョンの指定
		 *
		 * 指定しておかないと PermanentRedirect エラーが発生
		 * Status Code: 301, Error Code: PermanentRedirect
		 */
		client.setEndpoint("s3-ap-northeast-1.amazonaws.com");

		int multiplicity = Integer.parseInt(props.getProperty("s3_multiplicity"));

		ExecutorService executor = Executors.newFixedThreadPool(multiplicity);

		List<Future<PutObjectResult>> futureList = new ArrayList<Future<PutObjectResult>>();

		final String bucket = props.getProperty("s3_bucket");
		final String path = props.getProperty("s3_path");

		File[] files = new File(props.getProperty("img_dir")).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".jpg");
			}
		});

		for (final File file : files) {
			futureList.add(
				executor.submit(new Callable<PutObjectResult>() {
					public PutObjectResult call() {
						String key = path + "/" + file.getName();
						System.out.println(key);

						PutObjectRequest req = new PutObjectRequest(bucket, key, file);
						req.setCannedAcl(PublicRead);
						return client.putObject(req);
					}
				})
			);
		}

		executor.shutdown();

		for (Future<PutObjectResult> f : futureList) {
			try {
				PutObjectResult res = f.get();
				System.out.println("*** success " + res);
			} catch (Exception ex) {
				System.out.println("*** error : " + ex);
			}
		}
	}
}