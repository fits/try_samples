package fits.sample

import scalaz._
import Scalaz._

object CodensitySample extends App {

	// (1) Codensity[Function0, Int] で apply の結果は Function0[Unit]
	Codensity.pureCodensity(1).apply { (x) => () => println(x) }()

	// (2) Codensity[List, Int] で apply の結果は List[Int]
	Codensity.pureCodensity(2).apply { (x) => List(x) } |> println

	// (3) Codensity[Option, Int] で apply の結果は Option[Int]
	Codensity.pureCodensity(3).apply { Option(_) } |> println

	// (4) rep を使って List[Int] から Codensity[List, Int] を取得
	Codensity.rep(List(1, 2)).apply { (x) => List(x, x * 10,  x * 100) } |> println

	/* (5) バインド >>= を使うには improve すればよい
	 *   'Codensity.pureCodensity(5) >>= ・・・' とするとコンパイルエラー
	 */
	(Codensity.pureCodensity(5).improve >>= { (x) => Codensity.pureCodensity[Option, Int](x + 3) }) apply { (x) => Option(x * 10) } foreach(println)
}
