package fits.sample

import scalaz._, Scalaz._
import effect._, IO._
import iteratee._, Iteratee._

object ReadFileSample extends App {

	import java.io._

	val reader = new FileReader(args(0))
	val r = enumReader(reader)

	println("----- (1)")

	// 1文字目出力
	(head[IoExceptionOr[Char], IO] &= r).map {
		_ flatMap ( _.toOption )
	}.run.unsafePerformIO().foreach( println )

	println("----- (2)")

	// 2文字目出力
	(head[IoExceptionOr[Char], IO] &= r).map {
		_ flatMap ( _.toOption )
	}.run.unsafePerformIO().foreach( println )

	println("----- (3)")

	// 残りを全て出力
	(collect[IoExceptionOr[Char], Stream].up[IO] &= r).map {
		_ flatMap ( _.toOption )
	}.run.unsafePerformIO().foreach( print )

	println()
	println("----- (4)")

	// reader は close されていない
	IoExceptionOr(reader.ready()).fold(
		ex => println(s"### error : ${ex.getMessage}"),
		res => println(s"### success : ${res}")
	)

	IoExceptionOr(reader.close()).fold(
		ex => println(s"### error : ${ex.getMessage}"),
		_ => println("### success close")
	)

	IoExceptionOr(reader.ready()).fold(
		ex => println(s"### error : ${ex.getMessage}"),
		res => println(s"### success : ${res}")
	)
}
