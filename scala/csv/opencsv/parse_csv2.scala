import java.io.FileReader
import au.com.bytecode.opencsv.CSVReader

val reader = new CSVReader(new FileReader(args(0)))

Iterator.continually(reader.readNext).takeWhile(_ != null).map(_.toList).foreach {
	case no :: title :: content :: _ => println(title + " : " + content)
	case _ =>
}
