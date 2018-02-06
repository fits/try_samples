@Grab('org.apache.poi:poi-ooxml:3.17')
import org.apache.poi.ss.SpreadsheetVersion
import org.apache.poi.ss.usermodel.DataConsolidateFunction
import org.apache.poi.ss.util.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook

new XSSFWorkbook().withCloseable { book ->

	def sh1 = book.createSheet('Sheet1')

	def hdr = sh1.createRow(0)

	hdr.createCell(0).setCellValue('分類')
	hdr.createCell(1).setCellValue('アイテム')
	hdr.createCell(2).setCellValue('データ')

	def r1 = sh1.createRow(1)

	r1.createCell(0).setCellValue('A')
	r1.createCell(1).setCellValue('アイテム1')
	r1.createCell(2).setCellValue(1)

	def r2 = sh1.createRow(2)

	r2.createCell(0).setCellValue('A')
	r2.createCell(1).setCellValue('アイテム2')
	r2.createCell(2).setCellValue(2)

	def r3 = sh1.createRow(3)

	r3.createCell(0).setCellValue('B')
	r3.createCell(1).setCellValue('アイテム3')
	r3.createCell(2).setCellValue(3)

	def dataArea = new AreaReference('A1:C4', SpreadsheetVersion.EXCEL2007)

	def pt = sh1.createPivotTable(dataArea, new CellReference('F2'))

	pt.addRowLabel(0)
	pt.addRowLabel(1)

	pt.addColumnLabel(DataConsolidateFunction.SUM, 2, '合計:データ')

	def out = new FileOutputStream('pivot_sample.xlsx')

	book.write(out)

	out.close()
}
