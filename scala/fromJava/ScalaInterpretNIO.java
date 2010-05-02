import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import scala.tools.nsc.*;

public class ScalaInterpretNIO {
	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			System.out.println("java ScalaInterpretNIO [script file]");
			return;
		}

		FileChannel fc = new FileInputStream(args[0]).getChannel();
		MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		CharBuffer chb = Charset.defaultCharset().decode(buffer);

		Interpreter p = new Interpreter(new Settings());

		p.interpret(chb.toString());

		p.close();
	}
}
