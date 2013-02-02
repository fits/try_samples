package fits.sample

import scalaz._
import effect._
import iteratee._, Iteratee._

object EnumeratorUtil {
	// 1行ずつ読み出す Enumerator を作成する
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
}