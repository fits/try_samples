package fits.sample

import scalaz._, Scalaz._
import effect._, IO._
import iteratee._, Iteratee._

object ReadLineFileSample2 extends App {
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

	val reader = new BufferedReader(new FileReader(args(0)))

	val r = enumBufferedReader(reader)

	println("----- (1)")

	// (1) 1行目を出力
	(head[IoExceptionOr[String], IO] &= r).map {
		_ flatMap ( _.toOption )
	}.run.unsafePerformIO().foreach( println )

	println("----- (2)")

	// (2) 何も出力されない。(1) でファイルが close されてしまうため
	(collect[IoExceptionOr[String], Stream].up[IO] &= r).map {
		_ flatMap ( _.toOption )
	}.run.unsafePerformIO().foreach( println )

	// close の有無を確認（reader は close されている）
	IoExceptionOr(reader.ready()).fold(
		ex => println(s"### error : ${ex.getMessage}"),
		res => println(s"### success : ${res}")
	)

	println("----- (3)")

	val reader2 = new BufferedReader(new FileReader(args(0)))

	val r2 = enumBufferedReader(reader2)
	// (3) 全行出力
	(collect[IoExceptionOr[String], Stream].up[IO] &= r2).map {
		_ flatMap ( _.toOption )
	}.run.unsafePerformIO().foreach( println )


	println("----- (4)")

	(collect[IoExceptionOr[String], Stream].up[IO] &= r2).map {
		_ flatMap {
			_.valueOr("*** error")
		}
	}.run.unsafePerformIO().foreach( println )

	// close の有無を確認（reader2 は close されていない）
	IoExceptionOr(reader2.ready()).fold(
		ex => println(s"### error : ${ex.getMessage}"),
		res => println(s"### success : ${res}")
	)
}
