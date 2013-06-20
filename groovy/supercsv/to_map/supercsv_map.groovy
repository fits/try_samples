@Grab('net.sf.supercsv:super-csv:2.1.0')
import org.supercsv.io.CsvMapReader
import org.supercsv.prefs.CsvPreference

def data = '''name,point
"ユーザー1",100
"ユーザー2",80
'''

def reader = new StringReader(data)

def csv = new CsvMapReader(reader, CsvPreference.STANDARD_PREFERENCE)

def headers = csv.getHeader(true)

def res = null

while((res = csv.read(headers)) != null) {
	println res
}

