
import scala.io.Source
import scala.util.parsing.combinator._

object Csv extends JavaTokenParsers {
	override def skipWhitespace = false
//	以下でも可
//	override val whiteSpace = """[ \t]*""".r

	def csvFile = rep(line <~ eol)
	def line = repsep(cell, ',')
	//最後のセル要素に改行が含まれるので trim で取り除く
	def cell = quotedCell | """[^,\n]*""".r ^^ (_.trim)
	//quotedCellが行の最後に来た場合のみ \r\n になる
	def eol = "\n" | "\r\n"
	def quotedCell = '"' ~> quotedChars ~ rep(escapeQuotedChars) <~ '"' ^^ {case(x~xs) => x + xs.mkString}
	def quotedChars = """[^"]*""".r
	def escapeQuotedChars = "\"\"" ~> quotedChars ^^ ('"' + _)
}

val csv = Source.stdin.mkString
println(Csv.parseAll(Csv.csvFile, csv))
