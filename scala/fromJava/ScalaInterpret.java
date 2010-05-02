import scala.io.Source$;
import scala.tools.nsc.Interpreter;
import scala.tools.nsc.Settings;

public class ScalaInterpret {
	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println("java ScalaInterpret [script file]");
			return;
		}

		Interpreter p = new Interpreter(new Settings());

		//ファイルの内容を文字列で取得
		String script = Source$.MODULE$.fromFile(args[0]).mkString();

		p.interpret(script);

		p.close();
	}
}
