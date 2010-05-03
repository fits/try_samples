# coding: utf-8
#
#WPF 実行サンプル

import clr
clr.AddReference("PresentationFramework")
from System.Windows import *

app = Application()

win = Window()
win.Title = "てすと"
win.Height = 300
win.Width = 300

app.Run(win)

