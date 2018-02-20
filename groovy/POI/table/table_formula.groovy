@Grab('org.apache.poi:poi-ooxml:3.17')
@Grab('org.apache.poi:ooxml-schemas:1.3')
import org.apache.poi.xssf.usermodel.XSSFWorkbook

def fields = ['item', 'value', 'calc']

new XSSFWorkbook().withCloseable { book ->

	def sh1 = book.createSheet('Sheet1')

	def shtbl = sh1.createTable()
	shtbl.setName('table1')
	shtbl.setDisplayName('table1')

	def range = "A1:C2"

	def tbl = shtbl.getCTTable()
	tbl.setRef(range)
	tbl.setInsertRow(true)

	tbl.addNewAutoFilter().setRef(range)

	def cols = tbl.addNewTableColumns()
	cols.setCount(fields.size())

	def hdr = sh1.createRow(0)

	fields.eachWithIndex { v, i ->
		def col = cols.addNewTableColumn()
		col.setId(i + 1)
		col.setName(v)

		hdr.createCell(i).setCellValue(v)
	}

	cols.getTableColumnArray(fields.size() - 1)
		.addNewCalculatedColumnFormula().set("table1[value] * 2")

	def out = new FileOutputStream('table_formula.xlsx')

	book.write(out)

	out.close()
}
