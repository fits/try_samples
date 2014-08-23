
import java.io.BufferedReader;
import java.io.File;
import static java.nio.charset.StandardCharsets.*;

import fj.F;
import fj.Unit;
import fj.data.IO;
import fj.data.Option;
import static fj.data.IOFunctions.*;

class IoFileRead {
	public static void main(String... args) throws Exception {

		F<BufferedReader, String> readLine = br -> {
			try {
				return br.readLine();
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
		};

		IO<String> res = bracket(
			bufferedReader(new File(args[0]), Option.some(UTF_8)),
			br -> {
				System.out.println("*** close");
				return closeReader(br);
			},
			br -> {
				String line1 = readLine.f(br);
				readLine.f(br);
				String line3 = readLine.f(br);

				return unit(line1 + "," + line3);
			}
		);

		System.out.println(res.run());
	}
}
