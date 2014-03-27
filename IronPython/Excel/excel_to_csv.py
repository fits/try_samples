# coding: utf-8
#
# Excel の 1シート目を CSV 形式で出力するスクリプト
import sys

if len(sys.argv) < 3:
	print "ipy %s <excel file> <dest csv file>" % sys.argv[0]
	sys.exit()

from System import *
from System.IO import *

import clr
clr.AddReference("office")
clr.AddReference("Microsoft.Office.Interop.Excel")

from Microsoft.Office.Core import *
from Microsoft.Office.Interop.Excel import *

app = ApplicationClass()
book = app.Workbooks.Open(FileInfo(sys.argv[1]).FullName)

book.Sheets[1].SaveAs(FileInfo(sys.argv[2]).FullName, XlFileFormat.xlCSV)

book.Close(False)
app.Quit()
