import scala.io.Source

import java.io.File
import java.net.URL
import java.nio.file.{Paths, Files}
import java.nio.file.StandardCopyOption._

val dir = args(0)

Source.stdin.getLines.toArray.par.foreach {u =>
	val url = new URL(u)
	val filePath = Paths.get(dir, new File(url.getFile()).getName())

	try {
		Files.copy(url.openStream(), filePath, REPLACE_EXISTING)

		printf("downloaded: %s => %s\n", url, filePath)
	} catch {
		case e: Exception => printf("failed: %s, %s\n", url, e)
	}
}
