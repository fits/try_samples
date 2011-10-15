import scala.util.continuations._
import scala.actors.Actor
import scala.actors.Actor._
import scala.io.Source

import java.io.InputStream
import java.io.File
import java.net.URL
import java.nio.file.{Paths, Files, Path}
import java.nio.file.StandardCopyOption._

case class URLOpen(val url: URL, val k: (InputStream => Unit))
case class CreateFile(val filePath: Path, val stream: InputStream, val k: (Path => Unit))

class URLActor extends Actor {
	def act() {
		loop {
			react {
				case uo: URLOpen => {
					println("receive : " + uo.url)
					uo.k(uo.url.openStream())
				}
				case rs: CreateFile => {
					println("file create")
					Files.copy(rs.stream, rs.filePath, REPLACE_EXISTING)
					rs.k(rs.filePath)
				}
			}
		}
	}

	def stop() {
		exit()
	}
}

val dir = args(0)

Source.stdin.getLines.toList.par.foreach {u =>
	val url = new URL(u)

	reset {
		val actor = new URLActor()
		actor.start
		println("actor.start")

		val stream = shift {k: (InputStream => Unit) =>
			actor ! URLOpen(url, k)
			println("actor ! URLOpen")
		}

		println("stream = " + stream)

		val file = shift {k: (Path => Unit) =>
			val f = new File(url.getFile()).getName()
			val filePath = Paths.get(dir, f)

			actor ! CreateFile(filePath, stream, k)
			println("actor ! CreateFile")
		}

		println("downloaded: " + file)

		actor.stop
		println("actor.stop")
	}
}

println("*** out reset")
