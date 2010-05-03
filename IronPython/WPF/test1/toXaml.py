# coding: utf-8
#
#WPF オブジェクトを XAML に出力するサンプル

import clr
clr.AddReference("PresentationFramework")
from System import *
from System.Windows import *
from System.Windows.Markup import *

win = Window()
win.Title = "てすと"
win.Height = 300
win.Width = 300

Console.WriteLine(XamlWriter.Save(win))
