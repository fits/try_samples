import java.io.File

if (args.length < 1) {
	println("scala search_file2.scala [dir]")
	exit
}

def listFiles(f: File): List[File] = {
	if (f.isDirectory()) {
		f.listFiles().toList.flatMap(listFiles)
	}
	else {
		List(f)
	}
}

val list = listFiles(new File(args(0))).filter {f =>
	f.getName().endsWith(".scala")
}

list.foreach(println)
