
import java.io.*
import org.apache.poi.hssf.usermodel.*

wb = new HSSFWorkbook(new FileInputStream(args[0]))

for (i = 0; i < wb.numberOfSheets; i++) {
	println wb.getSheetName(i)
}
