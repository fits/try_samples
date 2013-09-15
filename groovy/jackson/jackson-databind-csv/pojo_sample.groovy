@Grab('com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.2.3')
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema

class Data {
	String id
	String name
	BigDecimal point
	Date date
}

def mapper = new CsvMapper()
def schema = mapper.schemaFor(Data)

println schema

def res = mapper.reader(Data).with(schema).readValue('1,test1,10,2013-09-12T10:15:00+0900')

println "id: ${res.id}, name: ${res.name}, point: ${res.point}, date: ${res.date}"

// 日付フォーマットを変更
mapper.setDateFormat(new java.text.SimpleDateFormat('yyyy/MM/dd HH:mm:ss'))

def res2 = mapper.reader(Data).with(schema).readValue('2,test2,20,2013/09/12 10:15:00')

println "id: ${res2.id}, name: ${res2.name}, point: ${res2.point}, date: ${res2.date}"
