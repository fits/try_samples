
import java.io.*;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class DownloadWeb {
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("java DownloadWeb <output dir>");
			return;
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		ExecutorService exec = Executors.newCachedThreadPool();

		final String dir = args[0];
		String url = null;

		while ((url = reader.readLine()) != null) {

			final URI uri = URI.create(url);
			String fileName = new File(uri.getPath()).getName();
			final Path filePath = Paths.get(dir, fileName);

			exec.submit(new Runnable() {
				@Override
				public void run() {
					try (InputStream in = uri.toURL().openStream()) {
						Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
						System.out.printf("completed: %s => %s\n", uri, filePath);
					} catch (Exception ex) {
						System.out.printf("failed: %s\n", uri);
					}
				}
			});
		}

		exec.shutdown();
	}
}