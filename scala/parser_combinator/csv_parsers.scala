
import scala.io.Source
import scala.util.parsing.combinator._

object CSVParsers extends JavaTokenParsers {
	override val whiteSpace = """[ \f\r\t]""".r

	lazy val csvFile: Parser[Any] = rep(cellContent <~ newLine)
	lazy val cellContent: Parser[Any] = repsep(cell, ',')
	lazy val cell: Parser[Any] = """[^\n]*""".r
	lazy val newLine: Parser[Any] = '\n'
}

val csv = Source.stdin.mkString
println(CSVParsers.parseAll(CSVParsers.csvFile, csv))

