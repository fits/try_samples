
import scala.io.Source
import scala.util.parsing.combinator._

object TomcatLogParsers extends JavaTokenParsers {
	override def skipWhitespace = false

	lazy val logFile = rep(logDate ~ eol ~ logContents)

	lazy val logDate = """([0-9]{2} [0-9]{2}, [0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2} .*)""".r

	lazy val logContents = rep(logContent ~ eol) ^^ (_.mkString("\n"))
	lazy val logContent = """.*""".r

	lazy val eol = "\n" | "\r\n"
}

val content = Source.fromFile(args(0), "Shift_JIS").mkString

println(TomcatLogParsers.parseAll(TomcatLogParsers.logFile, content))

