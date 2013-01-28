package fits.sample

import scalaz._, Scalaz._
import effect._, IO._
import iteratee._, Iteratee._

object ReadLineFileSample extends App {
	// ファイルから 1行読み出す Enumerator を作成
	def enumBufferedReader[F[_]](r: => java.io.BufferedReader)(implicit MO: MonadPartialOrder[F, IO]): EnumeratorT[IoExceptionOr[String], F] = {

		new EnumeratorT[IoExceptionOr[String], F] {
			import MO._

			lazy val reader = r

			def apply[A] = (s: StepT[IoExceptionOr[String], F, A]) => {
				s.mapCont(
					k => {
						val i = IoExceptionOr(reader.readLine)

						if (i exists (_ != null)) {
							k(elInput(i)) >>== apply[A]
						}
						else {
							s.pointI
						}
					}
				)
			}
		}
	}

	import java.io._

	val r = enumBufferedReader(new BufferedReader(new FileReader(args(0))))

	// 1行目を出力
	(head[IoExceptionOr[String], IO] &= r).map {
		_ flatMap ( _.toOption )
	}.run.unsafePerformIO().foreach( println )

	println("-----")

	/**
	 * 残りの全行出力
	 *
	 * collect[IoExceptionOr[String], IO] &= r とするとコンパイルエラーとなる
	 *
	 * "could not find implicit value for parameter mae: 
	 *  scalaz.Monoid[Option[scalaz.effect.IoExceptionOr[String]]]"
	 *
	 */
	(collect[IoExceptionOr[String], Stream].up[IO] &= r).map {
		_ flatMap ( _.toOption )
	}.run.unsafePerformIO().foreach( println )
}
