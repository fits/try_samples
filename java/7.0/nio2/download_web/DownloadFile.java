
import java.io.File;
import java.net.URI;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class DownloadFile {
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("java DownloadFile <url> <output dir>");
			return;
		}

		URI uri = new URI(args[0]);
		String dir = args[1];
		String fileName = new File(uri.getPath()).getName();

		try (
			FileChannel reader = FileChannel.open(Paths.get(uri), StandardOpenOption.READ);
		) {
			ByteBuffer buf = ByteBuffer.allocate((int)reader.size());

			reader.read(buf);

			buf.rewind();

			FileChannel writer = FileChannel.open(Paths.get(dir, fileName), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

			writer.write(buf);
		}
	}
}