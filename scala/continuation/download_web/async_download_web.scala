/*
 * 限定継続 + Actor による非同期 Web コンテンツダウンロード処理
 */

import scala.util.continuations._
import scala.actors.Actor
import scala.actors.Actor._
import scala.io.Source

import java.io.{InputStream, File}
import java.net.URL
import java.nio.file.{Paths, Files, Path}
import java.nio.file.StandardCopyOption._

val using = (st: InputStream) => (block: InputStream => Unit) => try {block(st)} finally {st.close()}

case class URLOpen(val url: URL, val k: (InputStream => Unit))
case class URLDownload(val url: URL, val stream: InputStream, 
				val destDir: String, val k: (Path => Unit))

class URLActor extends Actor {
	def act() {
		loop {
			react {
				case uo: URLOpen => {
					try {
						uo.k(uo.url.openStream())
					}
					catch {
						case e: Exception => failStop(e, uo.url)
					}
				}
				case rs: URLDownload => {
					val f = new File(rs.url.getFile()).getName()
					val filePath = Paths.get(rs.destDir, f)

					try {
						using (rs.stream) {stream =>
							Files.copy(stream, filePath, REPLACE_EXISTING)
						}
						rs.k(filePath)
					}
					catch {
						case e: Exception => failStop(e, rs.url)
					}
				}
			}
		}
	}

	def stop() {
		exit
	}

	def failStop(e: Exception, url: URL) {
		printf("failed: %s, %s\n", url, e)
		exit
	}
}

val dir = args(0)

//Source.stdin.getLines.toList.par.foreach {u =>
Source.stdin.getLines.toList.foreach {u =>
	val url = new URL(u)

	reset {
		val actor = new URLActor()
		actor.start

		//URL接続処理
		val stream = shift {k: (InputStream => Unit) =>
			actor ! URLOpen(url, k)
		}

		//ダウンロード処理
		val file = shift {k: (Path => Unit) =>
			actor ! URLDownload(url, stream, dir, k)
		}

		printf("downloaded: %s => %s\n", url, file)
		actor.stop
	}
}
