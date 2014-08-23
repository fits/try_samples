
import java.io.BufferedReader;
import java.io.File;
import static java.nio.charset.StandardCharsets.*;

import fj.F;
import fj.Unit;
import fj.data.IO;
import fj.data.Option;
import static fj.F1Functions.andThen;
import static fj.data.IOFunctions.*;

class IoFileRead2 {
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
			andThen(
				br -> {
					System.out.println("*** close");
					return br;
				},
				br -> closeReader(br)
			),
			br -> liftM2(
				apply(unit(br), unit(readLine)),
				append(
					apply(unit(br), unit(readLine)),
					apply(unit(br), unit(readLine))
				),
				(a, b) -> a + "," + b
			)
		);

		System.out.println(res.run());
	}
}
