
import static java.nio.charset.StandardCharsets.*;

import java.io.File;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;

class CommonsIoWriteFile {
	public static void main(String... args) throws Exception {
		String w1 = "サンプル1";
		String w2 = "サンプル2";

		File file = new File("sample_cio/1/sample1.txt");

		FileUtils.writeStringToFile(file, w1, UTF_8);
		// 以下でも可
		//FileUtils.write(file, w1, "UTF-8");

		// 追記
		FileUtils.writeStringToFile(file, w2, UTF_8, true);
		// 以下でも可
		//FileUtils.write(file, w2, "UTF-8", true);

		File file2 = new File("sample_cio/1/sample2.txt");
		// 2行出力
		FileUtils.writeLines(file2, "UTF-8", Arrays.asList(w1, w2));
	}
}