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

	// 以下でも可
	val f1a = for {
		a <- 2 * (_: Int)
		b <- 10 + (_: Int)
		c <- -5 + (_: Int)
	} yield a + b + c

	println(f1a(3))

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
