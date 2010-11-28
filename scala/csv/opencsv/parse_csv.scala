import scala.collection.JavaConversions._
import java.io.FileReader
import au.com.bytecode.opencsv.CSVReader

if (args.length < 1) {
	println("scala parse_csv.scala [csv file]")
	exit
}

val reader = new CSVReader(new FileReader(args(0)))

reader.readAll().map(_.toList).foreach {
	case no :: title :: content :: _ => println(title + " : " + content)
	case _ =>
}
