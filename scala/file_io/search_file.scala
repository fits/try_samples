
import java.io.File

def listFiles(dir: File)(proc: File => Unit): Unit = {

	dir.listFiles() match {
		case null =>
		case files: Array[File] => files foreach {f =>
			listFiles(f)(proc)
			proc(f)
		}
	}
}

if (args.length < 1) {
	println("scala search_file.scala [dir]")
	exit
}

listFiles(new File(args(0))) {f =>
	println(f)
}

