# coding: utf-8
#
# 指定ワークブックのシート名を出力するサンプル

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
app.Visible = MsoTriState.msoFalse

book = app.Workbooks.Open(FileInfo(sys.argv[1]).FullName)

for ws in book.Worksheets:
    print ws.Name
    
    for sh in ws.Shapes:
        try:
            print sh.TextFrame.Characters().Text
            print sh.Fill.ForeColor.SchemeColor
        except:
            print sh.Name


#ファイルを保存するか否かのダイアログ表示を避けるため False を指定
book.Close(False)

app.Quit()
