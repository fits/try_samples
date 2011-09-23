
import java.io.File;
import java.io.InputStream;
import java.net.URI;

import java.nio.file.Paths;
import java.nio.file.Files;

public class DownloadFile {
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("java DownloadFile <url> <output dir>");
			return;
		}

		URI uri = new URI(args[0]);
		String dir = args[1];
		String fileName = new File(uri.getPath()).getName();

		try (InputStream istream = uri.toURL().openStream()) {
			Files.copy(istream, Paths.get(dir, fileName));
		}
	}
}