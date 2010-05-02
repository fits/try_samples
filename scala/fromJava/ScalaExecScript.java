import scala.io.Source$;
import scala.tools.nsc.Interpreter;
import scala.tools.nsc.Settings;

public class ScalaExecScript {
	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("java ScalaExecScript [script file] [script]");
			return;
		}

		Interpreter p = new Interpreter(new Settings());

		//ファイルの内容を文字列で取得
		String script = Source$.MODULE$.fromFile(args[0]).mkString();

		boolean compiled = p.compileString(script);

		System.out.println("compile: " + compiled);

		if (compiled) {
			p.interpret(args[1]);
		}

		p.close();
	}
}
