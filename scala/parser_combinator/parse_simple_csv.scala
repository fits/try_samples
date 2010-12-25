
import scala.io.Source
import scala.util.parsing.combinator._

object SimpleCsv extends JavaTokenParsers {
	def csvFile: Parser[Any] = rep(line <~ eol)
	def line: Parser[Any] = repsep(cell, ',')
	//最後のセル要素に改行が含まれるので trim で取り除く
	def cell: Parser[Any] = """[^,\n]*""".r ^^ {x => x.trim()}
	def eol: Parser[Any] = '\n'
}

val csv = Source.stdin.mkString
println(SimpleCsv.parseAll(SimpleCsv.csvFile, csv))
