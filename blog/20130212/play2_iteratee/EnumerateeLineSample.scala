package fits.sample

import play.api.libs.iteratee._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import java.io.File
import java.nio.charset.StandardCharsets._

object EnumerateeLineSample extends App {
	import scala.concurrent.ExecutionContext.Implicits.global

	// (a) ファイルの内容を取得する Enumerator
	val enumerator = Enumerator.fromFile(new File(args(0)))

	// (b) 1行分の String を生成する Iteratee
	val takeLine = for {
		// (c) 改行文字までの入力を line へ代入
		line <- Enumeratee.takeWhile[Byte](_ != '\n'.toByte) &>> Iteratee.getChunks
		// (d) 改行文字を捨てる
		_    <- Enumeratee.take(1) &>> Iteratee.ignore[Byte]
	} yield new String(line.toArray, UTF_8)

	// (e) 行毎に先頭へ # を付けて出力する処理の組み立て
	val future = enumerator &> Enumeratee.mapConcat( _.toSeq ) &> Enumeratee.grouped(takeLine) |>>> Iteratee.foreach { s => 
		println(s"#${s}")
	}

	// (f) future の処理完了待ち
	Await.ready(future, Duration.Inf)

	// 強制的に終了するための処理
	/*
	import scala.collection.convert.WrapAsScala._
	Thread.getAllStackTraces().keySet().foreach(_.interrupt)
	*/
}
