
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
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		ExecutorService exec = Executors.newCachedThreadPool();

		final String dir = args[0];
		String url = null;

		while ((url = reader.readLine()) != null) {
			final URI uri = URI.create(url);
			final Path filePath = Paths.get(dir, new File(uri.getPath()).getName());

			exec.submit(new Runnable() {
				@Override
				public void run() {
					try (InputStream in = uri.toURL().openStream()) {
						Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
						System.out.printf("downloaded: %s => %s\n", uri, filePath);
					} catch (Exception e) {
						System.out.printf("failed: %s, %s\n", uri, e);
					}
				}
			});
		}

		exec.shutdown();
	}
}