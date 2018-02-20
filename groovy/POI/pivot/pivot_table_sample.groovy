@Grab('org.apache.poi:poi-ooxml:3.17')
@Grab('org.apache.poi:ooxml-schemas:1.3')
import org.apache.poi.ss.usermodel.DataConsolidateFunction
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFWorkbook

def fields = ['item', 'date', 'value', 'week-date']

new XSSFWorkbook().withCloseable { book ->

	def df = book.createDataFormat()

	def style = book.createCellStyle()
	style.setDataFormat(df.getFormat('yyyy-m-d'))

	def sh1 = book.createSheet('table')
	sh1.setDefaultColumnStyle(fields.indexOf('date'), style)
	sh1.setDefaultColumnStyle(fields.indexOf('week-date'), style)

	def shtbl = sh1.createTable()
	shtbl.setName('table1')
	shtbl.setDisplayName('table1')

	def range = "A1:${CellReference.convertNumToColString(fields.size() - 1)}2"

	def table = shtbl.getCTTable()
	table.setRef(range)
	table.setInsertRow(true)

	table.addNewAutoFilter().setRef(range)

	def cols = table.addNewTableColumns()
	cols.setCount(fields.size())

	def hdr = sh1.createRow(0)

	fields.eachWithIndex { v, i ->
		def col = cols.addNewTableColumn()
		col.setId(i + 1)
		col.setName(v)

		hdr.createCell(i).setCellValue(v)
	}

	cols.getTableColumnArray(fields.indexOf('week-date'))
		.addNewCalculatedColumnFormula()
		.set("table1[date] - WEEKDAY(table1[date], 3)")

	def sh2 = book.createSheet('pivot')

	def pt = sh2.createPivotTable(shtbl, new CellReference('A2'))

	pt.addRowLabel(fields.indexOf('item'))
	pt.addRowLabel(fields.indexOf('week-date'))

	pt.addColumnLabel(
		DataConsolidateFunction.SUM, 
		fields.indexOf('value'), 
		'sum:value'
	)

	def out = new FileOutputStream('pivot_table_sample.xlsx')

	book.write(out)

	out.close()
}
