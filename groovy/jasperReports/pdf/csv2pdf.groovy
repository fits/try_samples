/*
 * jasperreports の ivy-5.0.0.xml を直接編集して
 *
 *  (1) commons-collections のバージョンを 3.2.1 へ変更
 *  (2) iText のバージョンを 4.2.0 へ変更
 *  (3) xml-apis のバージョンを 2.0.2 へ変更
 *
 */
@Grab('net.sf.jasperreports:jasperreports:5.0.0')
import net.sf.jasperreports.engine.*
import net.sf.jasperreports.engine.data.*

if (args.length < 3) {
	println "<template file> <data csv file> <dest file>"
	return
}

def templateFileName = args[0]
def dataFileName = args[1]
def destFileName = args[2]

def report = JasperCompileManager.compileReport(templateFileName)

def ds = new JRCsvDataSource(dataFileName)
//先頭行がヘッダーの場合は true を設定
ds.useFirstRowAsHeader = true

def jsPrint = JasperFillManager.fillReport(report, [:], ds)

JasperExportManager.exportReportToPdfFile(jsPrint, destFileName)
