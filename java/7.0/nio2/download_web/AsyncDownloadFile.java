
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.net.URI;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class AsyncDownloadFile {
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("java AsyncDownloadFile <output dir> <url>");
			return;
		}

		final String dir = args[0];
		final URI uri = new URI(args[1]);
		final String fileName = new File(uri.getPath()).getName();

		ExecutorService es = Executors.newCachedThreadPool();

		final Future<InputStream> con = es.submit(new Callable<InputStream>() {
			public InputStream call() throws Exception {
				return uri.toURL().openStream();
			}
		});

		Future<List<Future<Integer>>> res = es.submit(new Callable<List<Future<Integer>>>() {
			public List<Future<Integer>> call() throws Exception {
				ArrayList<Future<Integer>> result = new ArrayList<>();

				try (BufferedInputStream bis = new BufferedInputStream(con.get())) {
					byte[] buf = new byte[10240];
					int pos = 0;
					int len = 0;

					AsynchronousFileChannel fc = AsynchronousFileChannel.open(Paths.get(dir, fileName), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

					while ((len = bis.read(buf, 0, buf.length)) > -1) {
						result.add(fc.write(ByteBuffer.wrap(buf, 0, len), pos));
						pos += len;
					}
				}
				return result;
			}
		});

		try {
			for (Future<Integer> f : res.get()) {
				f.get();
			}
			System.out.println("downloaded: " + uri);
		} catch (Exception ex) {
			System.out.println("failed: " + uri);
		}

		es.shutdown();
	}
}