@Grab('org.apache.poi:poi-ooxml:3.9')
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.*

def printCell(Cell cell) {
	println cell
	println "${cell.sheet.sheetName} - (${cell.rowIndex}, ${cell.columnIndex}) = ${cell.stringCellValue}"
}

def getCell(Workbook workBook, CellReference cellRef) {
	println cellRef

	/*
	 * Sheet.getRow() を使うと未定義の場合に null が返って都合が悪いので
	 * 代わりに CellUtil.getRow() を使用
	 */
	def row = CellUtil.getRow(cellRef.row, workBook.getSheet(cellRef.sheetName))
	CellUtil.getCell(row, cellRef.col)
	// 以下でも可
	//row.getCell(cellRef.col, Row.CREATE_NULL_AS_BLANK)
}

def getCell(Workbook workBook, String refName) {
	getCell(workBook, new CellReference(workBook.getName(refName).refersToFormula))
}

def getFirstCell(Workbook workBook, String refName) {
	getCell(workBook, new AreaReference(workBook.getName(refName).refersToFormula).firstCell)
}

def wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook(new File("sample1.xlsx").newInputStream())

Cell c1 = getCell(wb, 'sample1')
printCell c1

Cell c2 = getFirstCell(wb, 'sample2')
printCell c2

Cell c12 = getFirstCell(wb, 'sample1')
printCell c12

// 範囲に CellReference を使うと NumberFormatException が発生
//println new CellReference('Sheet1!$B$1:$F$5')
