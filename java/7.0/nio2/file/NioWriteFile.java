
import static java.nio.charset.StandardCharsets.*;
import static java.nio.file.StandardOpenOption.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

class NioWriteFile {
	public static void main(String... args) throws Exception {
		String w1 = "サンプル1";
		String w2 = "サンプル2";

		Path file = Paths.get("sample_nio/1/sample1.txt");

		// Files.write では親ディレクトリを作成しない
		if (Files.notExists(file.getParent())) {
			Files.createDirectories(file.getParent());
		}

		Files.write(file, w1.getBytes(UTF_8));
		// 追記
		Files.write(file, w2.getBytes(UTF_8), APPEND);

		Path file2 = Paths.get("sample_nio/1/sample2.txt");
		// 2行出力
		Files.write(file2, Arrays.asList(w1, w2), UTF_8);
	}
}