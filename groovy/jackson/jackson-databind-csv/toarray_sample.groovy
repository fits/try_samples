@Grab('com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.2.3')
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser

def mapper = new CsvMapper()
mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY)

def res = mapper.reader(Object[]).readValue('1,test1,10\n2,test2,20')

println res
