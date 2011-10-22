
import java.io.*;
import java.util.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import static java.nio.file.StandardCopyOption.*;

public class AsyncDownloadWeb {
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("java AsyncDownloadWeb <output dir>");
			return;
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		final ExecutorService exec = Executors.newCachedThreadPool();
		List<Future<?>> futureList = new ArrayList<>();

		final String dir = args[0];
		String urlString = null;

		while ((urlString = reader.readLine()) != null) {
			final URI uri = URI.create(urlString);

			//URL毎の並列実行
			futureList.add(exec.submit(new Runnable() {
				public void run() {
					//URL接続処理の非同期実行
					final Future<InputStream> stream = exec.submit(new Callable<InputStream>() {
						public InputStream call() throws Exception {
							return uri.toURL().openStream();
						}
					});

					//ダウンロード処理の非同期実行
					Future<Path> file = exec.submit(new Callable<Path>() {
						public Path call() throws Exception {
							String fileName = new File(uri.getPath()).getName();
							Path filePath = Paths.get(dir, fileName);

							Files.copy(stream.get(), filePath, REPLACE_EXISTING);
							return filePath;
						}
					});

					/* ダウンロード完了まで待機
					 * call 処理内で発生した例外は Future の get 時に
					 * throw される
					 */
					try {
						System.out.printf("downloaded: %s => %s\n", uri, file.get());
					} catch (Exception ex) {
						System.out.printf("failed: %s, %s\n", uri, ex);
					}
				}
			}));
		}

		/* 完了まで待機
		 *
		 * 以下を実施せずにいきなり shutdown を呼び出すと、
		 * run 処理内での submit 前に shutdown が
		 * 呼び出された場合に不都合があるため
		 */
		for (Future<?> f : futureList) {
			f.get();
		}

		//終了
		exec.shutdown();
	}
}