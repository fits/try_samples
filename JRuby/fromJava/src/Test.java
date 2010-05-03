
import java.io.*;

import org.apache.bsf.BSFManager;

public class Test {

	public static void main(String[] args) throws Exception {

		try {
			BSFManager.registerScriptingEngine("ruby", "org.jruby.javasupport.bsf.JRubyEngine", new String[]{"rb"});

			BSFManager manager = new BSFManager();

			manager.exec("ruby", "(java)", 0, 0, readToEnd("test.rb"));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static String readToEnd(String fileName) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(fileName));

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		String line = null;

		while((line = br.readLine()) != null) {
			pw.println(line);
		}

		pw.flush();

		String result = sw.toString();

		br.close();
		pw.close();

		return result;
	}

}