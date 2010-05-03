# coding: utf-8
#
# Windows Form のサンプル
#

import clr
clr.AddReference("System.Windows.Forms")
from System.Windows.Forms import *

def form_load(sender, e):
    print "form load %s" % e

def form_click(sender, e):
    print "クリック %s - %s" % (sender, e)

f = Form()

f.Load += form_load
f.Click += form_click

Application.Run(f)

