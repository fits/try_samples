import scala.io.Source

import java.io.File
import java.net.URL
import java.nio.file.{Paths, Files}
import java.nio.file.StandardCopyOption._

val dir = args(0)

Source.stdin.getLines.toList.par.foreach {u =>
	val url = new URL(u)
	val f = new File(url.getFile()).getName()
	val filePath = Paths.get(dir, f)

	try {
		Files.copy(url.openStream(), filePath, REPLACE_EXISTING)

		printf("completed: %s => %s\n", url, filePath)

	} catch {
		case e: Exception => printf("failed: %s, %s\n", url, e)
	}
}
