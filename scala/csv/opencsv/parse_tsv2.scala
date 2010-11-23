import scala.collection.JavaConversions._
import java.io.FileReader
import au.com.bytecode.opencsv.CSVReader

if (args.length < 1) {
	println("scala parse_tsv2.scala [csv file]")
	exit
}

val reader = new CSVReader(new FileReader(args(0)), '\t')

reader.readAll().map(l => l.toList).foreach {
	case no :: name :: addr :: _ => println("address : " + addr.trim())
	case _ =>
}
