import scala.io.Source

if (args.length < 1) {
	println("scala read_file.scala [filename]")
	exit
}

Source.fromFile(args(0)).getLines.foreach(println)

println("--------------------------------")

//以下でも可
Source.fromFile(args(0)).getLines.foreach {l =>
	println(l)
}
