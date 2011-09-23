import scala.io.Source

import java.io.File
import java.net.URL
import java.nio.file.{Paths, Files}

val dir = args(0)

Source.stdin.getLines.toList.par.foreach {u =>
	val url = new URL(u)
	val f = new File(url.getFile()).getName()

	Files.copy(url.openStream(), Paths.get(dir, f))
}
