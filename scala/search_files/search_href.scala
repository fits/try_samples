import java.io.File
import scala.io.Source

if (args.length < 2) {
	println("scala search_href.scala [dir] [extension]")
	exit
}

def listAllFiles(extension: String)(f: File): List[File] = {
	if (f.isDirectory()) {
		f.listFiles().toList.flatMap(listAllFiles(extension))
	}
	else {
		List(f).filter(_.getName().endsWith(extension))
	}
}

implicit def toSource(f: File): Source = {
	Source.fromFile(f)("UTF-8")
}

val Href = """href="([^"]+)"""".r
case class Link(href: String, file: File)

val links = listAllFiles(args(1))(new File(args(0))).flatMap {f =>
	Href.findAllIn(f.mkString).toList.map {s =>
		val Href(href) = s
		Link(href, f)
	}
}

println("total: " + links.length)
links.foreach {link =>
	println(link.href + ", " + link.file.getName())
}
