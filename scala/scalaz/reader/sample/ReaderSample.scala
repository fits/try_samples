package fits.sample

import scalaz._
import Scalaz._

object ReaderSample extends App {

	/* Reader[Int, Int] とすると _ が省略可能
	 * Reader とすると _ の型指定が必要
	 */
	val f1 = for {
		a <- Reader[Int, Int]( 2 * )
		b <- Reader[Int, Int]( 10 + )
		c <- Reader( (_: Int) - 5 )
	} yield a + b + c

	println(f1(3))

	// for と同等処理
	val f1b = Reader[Int, Int]( 2 * ) >>= { a =>
		Reader[Int, Int]( 10 + ) >>= { b =>
			Reader[Int, Int]( -5 + ) >>= { c =>
				Reader(_ => a + b + c)
			}
		}
	}

	println(f1b(3))
}
