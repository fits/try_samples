
import java.io.*
import org.apache.poi.hssf.usermodel.*

wb = new HSSFWorkbook()

sh1 = wb.createSheet("テストシート")

cell = {row, col -> sh1.createRow(row).createCell((short)col)}

for (i in 0..9) {
	cl = cell(i, 0)
	cl.cellStyle.wrapText = true
	cl.setCellValue("テストデータ${i}")
}

outfile = new FileOutputStream("test.xls")

wb.write(outfile)
outfile.close()

