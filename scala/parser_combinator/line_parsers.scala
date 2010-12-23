
import scala.util.parsing.combinator._

object LineParsers extends JavaTokenParsers {
	lazy val file: Parser[Any] = rep(cellContent)
	lazy val cellContent: Parser[Any] = stringLiteral | floatingPointNumber
}

val body = """
"aaa"
123
"test"
"テスト"
-10.56"""

println(LineParsers.parseAll(LineParsers.file, body))

