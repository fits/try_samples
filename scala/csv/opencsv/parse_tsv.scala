import scala.collection.JavaConversions._
import java.io.FileReader
import au.com.bytecode.opencsv.CSVReader

if (args.length < 1) {
	println("scala parse_tsv.scala [csv file]")
	exit
}

val reader = new CSVReader(new FileReader(args(0)), '\t')

reader.readAll().foreach {l =>
	println("address : " + l(2).trim)
}
