
import scala.io.Source
import scala.util.parsing.combinator._

object Csv extends JavaTokenParsers {
	override def skipWhitespace = false
//	以下でも可
//	override val whiteSpace = """[ \t]*""".r

	def csvFile: Parser[Any] = rep(line <~ eol)
	def line: Parser[Any] = repsep(cell, ',')
	//最後のセル要素に改行が含まれるので trim で取り除く
	def cell: Parser[Any] = quotedCell | """[^,\n]*""".r ^^ {x => x.trim()}
	//quotedCellが行の最後に来た場合のみ \r\n になる
	def eol: Parser[Any] = "\n" | "\r\n"
	def quotedCell: Parser[Any] = '"' ~> quotedChars <~ '"'
	def quotedChars: Parser[Any] = """[^"]*""".r
}

val csv = Source.stdin.mkString
println(Csv.parseAll(Csv.csvFile, csv))
