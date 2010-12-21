
import scala.util.parsing.combinator._

object CSVParsers extends JavaTokenParsers {
	lazy val file: Parser[Any] = rep(cellContent ~ newLine | cellContent)
	lazy val cellContent: Parser[Any] = stringLiteral | floatingPointNumber
	lazy val newLine: Parser[Any] = '\n'
}

val body = """
"aaa"
123
"test"
"テスト"
-10.56"""

println(CSVParsers.parseAll(CSVParsers.file, body))

