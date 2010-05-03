import java.io.*;
import javax.script.*;

public class ScriptTest {

	public static void main(String[] args) {

		ScriptEngineManager manager = new ScriptEngineManager();

		for (ScriptEngineFactory factory : manager.getEngineFactories()) {
			System.out.println("-----------------------");
			System.out.printf("script engine name:%s, version:%s, language:%s, laguageversion:%s, class:%s \n", factory.getEngineName(), factory.getEngineVersion(), factory.getLanguageName(), factory.getLanguageVersion(), factory.getClass());
			for (String ext : factory.getExtensions()) {
				System.out.printf("ext:%s \n", ext);
			}
		}

		ScriptEngine engine = manager.getEngineByExtension("js");

		try {
			System.out.println("--- exe script ---");
			Object obj = engine.eval("print(\"hello\")");

			System.out.println("eval : " + obj);
		} catch (ScriptException ex) {
			ex.printStackTrace();
		}

		try {
			Object obj = engine.eval(new FileReader("tester.js"));
			System.out.println("eval : " + obj);

			Invocable inv = (Invocable)engine;
			Tester tester = inv.getInterface(Tester.class);

			System.out.println("message : " + tester.getMessage());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}