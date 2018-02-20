@Grab('org.apache.poi:poi-ooxml:3.17')
@Grab('org.apache.poi:ooxml-schemas:1.3')
import org.apache.poi.ss.SpreadsheetVersion
import org.apache.poi.ss.util.AreaReference
import org.apache.poi.ss.util.CellReference
import org.apache.poi.ss.util.CellUtil
import org.apache.poi.xssf.usermodel.XSSFWorkbook

def file = args[0]
def input = new FileInputStream(file)

new XSSFWorkbook(input).withCloseable { book ->
	input.close()

	def sh = book.getSheet('table')

	def shtbl = sh.getTables().head()

	println "row count: ${shtbl.rowCount}, endindex: ${shtbl.endRowIndex}, startindex: ${shtbl.startRowIndex}, row: ${sh.getRow(shtbl.endRowIndex)}"

	def formula = shtbl.getCTTable().getTableColumns().getTableColumnArray(3).getCalculatedColumnFormula().getStringValue()

	def rindex = (sh.getRow(shtbl.endRowIndex) == null) ? shtbl.endRowIndex : shtbl.endRowIndex + 1

	def r = sh.createRow(rindex)

	CellUtil.createCell(r, 0, 'A', sh.getColumnStyle(0))
	CellUtil.createCell(r, 1, '2018-3-1', sh.getColumnStyle(1))

	def valueCell = r.createCell(2)
	valueCell.setCellStyle(sh.getColumnStyle(2))
	valueCell.setCellValue(5)

	def weekCell = r.createCell(3)
	weekCell.setCellStyle(sh.getColumnStyle(3))
	weekCell.setCellFormula(formula)

	if (rindex != shtbl.endRowIndex) {
		def ref = shtbl.getCellReferences()

		shtbl.setCellReferences(
			new AreaReference(
				ref.getFirstCell(),
				new CellReference(rindex, ref.getLastCell().getCol()),
				SpreadsheetVersion.EXCEL2007
			)
		)
	}

	def out = new FileOutputStream(file)

	book.write(out)

	out.close()
}
