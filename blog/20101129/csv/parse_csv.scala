import java.io.FileReader
import au.com.bytecode.opencsv.CSVReader

val reader = new CSVReader(new FileReader(args(0)))

Iterator.continually(reader.readNext).takeWhile(_ != null).foreach {r =>
	println(r(0) + " : " + r(2))
}
