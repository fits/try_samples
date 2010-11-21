import java.io.File
import scala.io.Source

if (args.length < 1) {
	println("scala parse_tsv.scala [tsv file]")
	exit
}

Source.fromFile(new File(args(0))).getLines().foreach {l =>
	val items = l.split('\t').map(s => s.trim)

	items.length match {
		case 1 => println(items(0))
		case i if i > 1 => println(items(1))
	}
}

