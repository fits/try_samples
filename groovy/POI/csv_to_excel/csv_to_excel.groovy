@Grab('net.sf.supercsv:super-csv:2.1.0')
@Grab('org.apache.poi:poi-ooxml:3.9')
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.*
import org.supercsv.io.CsvMapReader
import org.supercsv.prefs.CsvPreference

def readCsv(Reader reader) {
	def result = []

	def csv = new CsvMapReader(reader, CsvPreference.STANDARD_PREFERENCE)

	def headers = csv.getHeader(false)

	def res = null

	while((res = csv.read(headers)) != null) {
		result << res
	}

	result
}

def getCell(Workbook workBook, CellReference cellRef) {
	def row = CellUtil.getRow(cellRef.row, workBook.getSheet(cellRef.sheetName))
	if (row) {
		CellUtil.getCell(row, cellRef.col)
	}
}

def getFirstCell(Workbook workBook, String refName) {
	def name = workBook.getName(refName)

	if (name) {
		getCell(workBook, new AreaReference(name.refersToFormula).firstCell)
	}
}

def list = readCsv new FileReader(args[0])

def workBook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(new File(args[1]).newInputStream())


list.first().each { k, v ->
	def cell = getFirstCell(workBook, k)

	if (cell) {
		try {
			// Long ’l‚É•ÏŠ·‚Å‚«‚éê‡‚Í Long ‚Åİ’è
			cell.setCellValue(Long.parseLong(v))
		} catch (NumberFormatException e) {
			// •¶š—ñ‚Æ‚µ‚Äİ’è
			cell.setCellValue(v)
		}
	}
}

new File('result.xlsx').withOutputStream {
	workBook.write(it)
}
