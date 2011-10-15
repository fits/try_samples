import java.io.*;
import java.util.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import static java.nio.file.StandardOpenOption.*;

public class AsyncDownloadWeb {
	public static void main(String[] args) throws Exception {

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		final ExecutorService exec = Executors.newCachedThreadPool();
		List<Future<?>> futureList = new ArrayList<>();

		final String dir = args[0];
		String urlString = null;

		while ((urlString = reader.readLine()) != null) {

			final URI uri = URI.create(urlString);
			String fileName = new File(uri.getPath()).getName();
			final Path filePath = Paths.get(dir, fileName);

			//URL毎の並列実行
			futureList.add(exec.submit(new Runnable() {
				public void run() {
					//URL接続処理の非同期実行
					final Future<InputStream> con = exec.submit(new Callable<InputStream>() {
						public InputStream call() throws Exception {
							return uri.toURL().openStream();
						}
					});

					//Webコンテンツ取得の非同期実行
					Future<List<Future<Integer>>> outputFutureList = exec.submit(new Callable<List<Future<Integer>>>() {
						public List<Future<Integer>> call() throws Exception {
							List<Future<Integer>> result = new ArrayList<>();

							try (BufferedInputStream bis = new BufferedInputStream(con.get())) {
								byte[] buf = new byte[10240];
								int pos = 0;
								int len = 0;

								AsynchronousFileChannel fc = AsynchronousFileChannel.open(filePath, CREATE, WRITE);

								while ((len = bis.read(buf, 0, buf.length)) > -1) {
									//ファイル出力の非同期実行
									result.add(fc.write(ByteBuffer.wrap(buf, 0, len), pos));
									pos += len;
								}
							}
							return result;
						}
					});

					/* ファイル出力完了まで待機
					 * call 処理内で発生した例外は Future の get 時に
					 * throw される
					 */
					try {
						for (Future<Integer> f : outputFutureList.get()) {
							f.get();
						}
						System.out.printf("downloaded: %s => %s\n", uri, filePath);
					} catch (Exception ex) {
						System.out.printf("failed: %s\n", uri);
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