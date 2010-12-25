
import scala.io.Source
import scala.util.parsing.combinator._

object SimpleCsv extends JavaTokenParsers {
	def csvFile = rep(line <~ eol)
	def line = repsep(cell, ',')
	//最後のセル要素に改行が含まれるので trim で取り除く
	def cell = """[^,\n]*""".r ^^ (_.trim)
	def eol = '\n'
}

val csv = Source.stdin.mkString
println(SimpleCsv.parseAll(SimpleCsv.csvFile, csv))
