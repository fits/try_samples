import java.io.FileReader
import au.com.bytecode.opencsv.CSVReader

def reader = new CSVReader(new FileReader(args[0]))

while((r = reader.readNext()) != null) {
	println "${r[0]} : ${r[2]}"
}

