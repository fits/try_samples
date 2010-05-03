# coding: utf-8
#
# WMEncoder を使用してメディアファイルの構成ファイルを出力するサンプル
#

import sys

if len(sys.argv) < 3:
    print "configexport.py [input media file name] [output config file name]"
    sys.exit()

import clr
clr.AddReference("Interop.WMEncoderLib")
from WMEncoderLib import *

editor = WMEncBasicEditClass()

editor.MediaFile = sys.argv[1]
editor.SaveConfigFile(sys.argv[2])

