import scala.xml._
import scalax.file._

case class AssertionResult(val name: String, val failure: Boolean, val error: Boolean)

case class HttpSample(val name: String, val rc: String, val time: Int, val lbType: String, val assertions: Seq[AssertionResult], val filePath: String)

object JMeterLogAnalyzer extends App {
	val logFiles = Path(".") ** s"${args(0)}*"

	val logs = logFiles.map( _.path ).toList.flatMap {p =>
		val filePath = p.toString
		val doc = XML.loadFile(filePath)

		(doc \\ "httpSample").map {s =>
			val tn = (s \ "@tn").head.text
			val rc = (s \ "@rc").head.text
			val t = (s \ "@t").head.text.toInt
			val lb = (s \ "@lb").head.text
			val ar = (s \ "assertionResult").map {a =>
				val an = (a \ "name").head.text
				val af = (a \ "failure").head.text.toBoolean
				val ae = (a \ "error").head.text.toBoolean

				AssertionResult(an, af, ae)
			}

			HttpSample(tn, rc, t, lb, ar, filePath)
		}
	}

	println(s"total : ${logs.length}")

	val tout = logs.filter(_.time >= 15000)
	println(s"15s timeout : ${tout.length}")

	val failed = logs.filter {l =>
		l.rc == "200" && l.assertions.foldLeft(false){(b, a) => b || a.failure}
	}
	println(s"failed: ${failed.length}")

	val errRc = logs.filter(_.rc != "200")
	println(s"rc != 200 : " + errRc.foldLeft(0){(b, a) => b + 1})

	println(s"total error : ${failed.length + errRc.length}")

	println("--- err details ---")
	errRc.groupBy(_.rc).foreach { case (a, b) =>
		println(s"$a : ${b.length}")
	}

	println("--- SocketException details ---")
	val sockErr = logs.filter(_.rc == "Non HTTP response code: java.net.SocketException")
	sockErr.groupBy(_.lbType).foreach { case (a, b) =>
		println(s"$a : ${b.length}")
	}
}
