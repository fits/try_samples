import java.io.*
import org.apache.poi.hssf.usermodel.*

//コンストラクタ追加
HSSFWorkbook.metaClass.constructor << {String xlsFileName ->
    new HSSFWorkbook(new FileInputStream(xlsFileName))
}

//メソッド追加
HSSFSheet.metaClass.getCell << {row, col -> 
    rowObj = getRow(row)
    if (rowObj == null) rowObj = createRow(row)

    cellObj = rowObj.getCell((short)col)
    return (cellObj != null)? cellObj: rowObj.createCell((short)col)
}

//プロパティ追加
HSSFCell.metaClass.getText << { ->
    richStringCellValue.string
}

//プロパティ追加
HSSFCell.metaClass.setText << {text ->
    setCellValue(new HSSFRichTextString(text))
}

wb = new HSSFWorkbook("test.xls")
sh = wb.getSheet("テストシート")

cell = {row, col -> sh.getCell(row, col)}

last_row = {sheet -> sheet.getRow(sheet.lastRowNum)}

println cell(3, 0).text

println "最後のセル (${sh.lastRowNum}, ${last_row(sh).lastCellNum})"

cell(3, 0).text = "テスト"
cell(20, 3).text = "ちぇっく・・・"

outfile = new FileOutputStream("test_new.xls")

wb.write(outfile)
outfile.close()
