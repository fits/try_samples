# coding: utf-8
#
# Excel のワークシートを開くサンプル

import sys

if len(sys.argv) < 2:
    print ">ipy %s [input file name]" % sys.argv[0]
    sys.exit()


from System import *
from System.IO import *

import clr
clr.AddReference("office")
clr.AddReference("Microsoft.Office.Interop.Excel")

from Microsoft.Office.Core import *
from Microsoft.Office.Interop.Excel import *

app = ApplicationClass()
app.Visible = MsoTriState.msoTrue

book = app.Workbooks.Open(FileInfo(sys.argv[1]).FullName)

print book.Name

book.Close()

app.Quit()
