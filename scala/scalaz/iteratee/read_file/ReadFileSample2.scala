package fits.sample

import scalaz._, Scalaz._
import effect._, IO._
import iteratee._, Iteratee._

import java.io.FileReader

object ReadFileSample2 extends App {

	val reader = new FileReader("data.txt")
	val enumerator = enumReader(reader)

	val enumeratee1: EnumerateeT[IoExceptionOr[Char], Int, IO] = map { c =>
		c.valueOr('0').toString.toInt * 2
	}

	val enumeratee2: EnumerateeT[IoExceptionOr[Char], Int, IO] = map { c =>
		c.valueOr('0').toString.toInt + 10
	}

	val iteratee1 = take[Int, Stream](3).up[IO]
	val iteratee2 = collect[Int, Stream].up[IO]

	println("---")

	// 2, 4, 6 が出力（1 * 2, 2 * 2, 3 * 2） 
	(iteratee1 %= enumeratee1 &= enumerator).run.unsafePerformIO().foreach(println)

	println("---")

	// 15, 16 が出力（5 + 10, 6 + 10）
	// 	4 は take で消費されてしまう模様
	(iteratee2 %= enumeratee2 &= enumerator).run.unsafePerformIO().foreach(println)

	IoExceptionOr(reader.close)
}
