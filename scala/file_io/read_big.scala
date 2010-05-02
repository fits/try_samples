import scala.io.Source

if (args.length < 1) {
	println("scala read_big.scala [filename]")
	exit
}

var i = 0
Source.fromFile(args(0), "Shift_JIS").getLines.foreach {l =>
	i += 1
	println("count : " + i)
	println(l)
}
