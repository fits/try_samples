import scala.io.Source

import java.io.{File, InputStream}
import java.net.URL
import java.nio.file.{Paths, Files}
import java.nio.file.StandardCopyOption._

val dir = args(0)

val using = (st: InputStream) => (block: InputStream => Unit) => try {block(st)} finally {st.close()}

Source.stdin.getLines.toList.par.foreach {u =>
	val url = new URL(u)
	val f = new File(url.getFile()).getName()
	val filePath = Paths.get(dir, f)

	try {
		using (url.openStream()) {stream =>
			Files.copy(stream, filePath, REPLACE_EXISTING)
		}

		printf("completed: %s => %s\n", url, filePath)

	} catch {
		case e: Exception => printf("failed: %s, %s\n", url, e)
	}
}
