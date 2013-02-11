package fits.sample

import play.api.libs.iteratee._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import java.io.File
import java.nio.charset.StandardCharsets._

object EnumerateeLineSample2 extends App {
	import scala.concurrent.ExecutionContext.Implicits.global

	val enumerator = Enumerator.fromFile(new File(args(0)))

	val takeLine = for {
		line <- Enumeratee.takeWhile[Byte](_ != '\n'.toByte) &>> Iteratee.getChunks
		_    <- Enumeratee.take(1) &>> Iteratee.ignore[Byte]
	} yield new String(line.toArray, UTF_8)

	// 1行目を捨てて 2行目から 2行取り出し
	// 各行の先頭へ # を付けて出力する処理の組み立て
	val future = enumerator &> Enumeratee.mapConcat( _.toSeq ) &> Enumeratee.grouped(takeLine) &> Enumeratee.drop(1) &> Enumeratee.take(2) |>>> Iteratee.foreach { s => 
		println(s"#${s}")
	}

	// future の処理完了待ち
	Await.ready(future, Duration.Inf)
}
