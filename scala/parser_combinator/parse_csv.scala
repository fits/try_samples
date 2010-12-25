
import scala.io.Source
import scala.util.parsing.combinator._

object Csv extends JavaTokenParsers {
//	override val whiteSpace = """[ \t\r]*""".r
	override def skipWhitespace = false

	def csvFile: Parser[Any] = rep(line <~ eol)
	def line: Parser[Any] = repsep(cell, ',')
	//最後のセル要素に改行が含まれるので trim で取り除く
	def cell: Parser[Any] = quotedCell | """[^,\n]*""".r ^^ {x => x.trim()}
	def eol: Parser[Any] = '\n'
	def quotedCell: Parser[Any] = '"' ~> quotedChars <~ '"'
	def quotedChars: Parser[Any] = """[^"]*""".r
}

val csv = Source.stdin.mkString
println(Csv.parseAll(Csv.csvFile, csv))
