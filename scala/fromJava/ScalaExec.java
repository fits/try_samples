import java.io.*;
import scala.tools.nsc.*;


public class ScalaExec {
	public static void main(String[] args) {

		Interpreter p = new Interpreter(new Settings());

		p.bind("label", "Int", 4);
		p.interpret("println(\"test\" + label)");

		p.close();
	}
}
