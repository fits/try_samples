
import java.io.*;
import java.util.ArrayList;
import java.net.URI;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;

public class AsyncDownloadFile {
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("java AsyncDownloadFile <output dir> <url>");
			return;
		}

		String dir = args[0];
		URI uri = new URI(args[1]);

		String fileName = new File(uri.getPath()).getName();

		ArrayList<Future<Integer>> futures = new ArrayList<>();

		try (BufferedInputStream bis = new BufferedInputStream(uri.toURL().openStream())) {
			byte[] buf = new byte[10240];
			int len = 0;
			int pos = 0;

			AsynchronousFileChannel fc = AsynchronousFileChannel.open(Paths.get(dir, fileName), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

			while ((len = bis.read(buf, 0, buf.length)) > -1) {
				futures.add(fc.write(ByteBuffer.wrap(buf, 0, len), pos));
				pos += len;
			}
		}

		for (Future<Integer> f : futures) {
			f.get();
		}
	}
}