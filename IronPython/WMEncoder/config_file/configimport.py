# coding: utf-8
#
# WMEncoder を使用してメディアファイルに構成ファイルの内容を適用するサンプル
#

import sys

if len(sys.argv) < 4:
    print "configimport.py [input media file name] [input config file name] [output media file name]"
    sys.exit()

from System.Threading import *

import clr
clr.AddReference("Interop.WMEncoderLib")
from WMEncoderLib import *

editor = WMEncBasicEditClass()

editor.MediaFile = sys.argv[1]
editor.ConfigFile = sys.argv[2]
editor.OutputFile = sys.argv[3]

editor.Start()

print "Start %s - error: %s" % (editor.RunState, editor.ErrorState)

while editor.ProgressPercent < 100:
    Thread.Sleep(1000)
