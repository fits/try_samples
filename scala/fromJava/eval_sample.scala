
import scala.io.Source
import scala.tools.nsc.Interpreter
import scala.tools.nsc.Settings

object EvalSample {
	def main(args: Array[String]) {
		val p = new Interpreter(new Settings())

		val res = p.interpret(Source.fromFile(args(0)).mkString)

		println("result" + res)

		p.close()
	}
}

EvalSample.main(args)
