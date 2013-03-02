package fits.sample

import scalaz._, Scalaz._
import effect._, IO._
import iteratee._, Iteratee._

object ReadLineFileSample3 extends App {
	// ファイルから 1行読み出す Enumerator を作成
	def enumBufferedReader[F[_]](r: => java.io.BufferedReader)(implicit MO: MonadPartialOrder[F, IO]): EnumeratorT[IoExceptionOr[String], F] = {

		new EnumeratorT[IoExceptionOr[String], F] {
			import MO._

			lazy val reader = r

			def apply[A] = (s: StepT[IoExceptionOr[String], F, A]) => {
				s.mapContOr(
					k => {
						val i = IoExceptionOr(reader.readLine)

						if (i exists (_ != null)) {
							k(elInput(i)) >>== apply[A]
						}
						else {
							s.pointI
						}
					},
					{
						println("*** close")
						IoExceptionOr(reader.close)
						s.pointI
					}
				)
			}
		}
	}

	import java.io._

	val r = enumBufferedReader(new BufferedReader(new FileReader(args(0))))

	val iter = for {
		_ <- drop[IoExceptionOr[String], IO](1)
		x <- take[IoExceptionOr[String], Stream](3).up[IO]
	} yield x

	(iter &= r).map {
		_ flatMap ( _.toOption )
	}.run.unsafePerformIO().foreach( s => println(s"#${s}") )

}
