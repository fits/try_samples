# coding: utf-8

import sys

if len(sys.argv) < 3:
    print ">ipy %s [input file name] [output file name]" % sys.argv[0]
    sys.exit()


from System import *
from System.IO import *

import clr
clr.AddReference("office")
clr.AddReference("Microsoft.Office.Interop.PowerPoint")

from Microsoft.Office.Core import *
from Microsoft.Office.Interop.PowerPoint import *

ppApp = ApplicationClass()
ppApp.Visible = MsoTriState.msoTrue

pr = ppApp.Presentations.Open(FileInfo(sys.argv[1]).FullName)

pr.SaveAs(FileInfo(sys.argv[2]).FullName, PpSaveAsFileType.ppSaveAsHTML, MsoTriState.msoFalse)

pr.Close()

ppApp.Quit()
