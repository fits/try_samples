/*
 * 限定継続 + ops による非同期 Web コンテンツダウンロード処理
 */

import scala.concurrent.ops
import scala.util.continuations._
import scala.io.Source

import java.io.{InputStream, File}
import java.net.URL
import java.nio.file.{Paths, Files, Path}
import java.nio.file.StandardCopyOption._

val dir = args(0)

Source.stdin.getLines.toList.foreach {u =>
	val url = new URL(u)

	reset {
		//URL接続処理
		val stream = shift {k: (InputStream => Unit) =>
			ops.spawn {
				println("===" + url)
				try {
					k(url.openStream())
				}
				catch {
					case e: Exception => printf("failed: %s, %s\n", url, e)
				}
			}
		}
		println("--------------" + url)

		//ダウンロード処理
		val file = shift {k: (Path => Unit) =>
			ops.spawn {
				println("+++" + url)
				val f = new File(url.getFile()).getName()
				val filePath = Paths.get(dir, f)

				try {
					Files.copy(stream, filePath, REPLACE_EXISTING)
					k(filePath)
				}
				catch {
						case e: Exception => printf("failed: %s, %s\n", url, e)
				}
			}
		}

		printf("downloaded: %s => %s\n", url, file)
	}
}
