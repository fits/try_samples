# coding: utf-8
#
# WMEncoder を使用してファイルを変換するサンプル
# エンコードのプロファイルに .prx ファイルを使用
#

import sys

if len(sys.argv) < 4:
    print "convert_prx.py [input file name] [output file name] [profile file]"
    sys.exit()

from System import *
from System.IO import *
from System.Threading import *

import clr
clr.AddReference("Interop.WMEncoderLib")
from WMEncoderLib import *


app = WMEncoderAppClass()

enc = app.Encoder

sg = enc.SourceGroupCollection.Add("sg1")

wmvFile = FileInfo(sys.argv[1])

vsrc = sg.AddSource(WMENC_SOURCE_TYPE.WMENC_VIDEO)
vsrc.SetInput(wmvFile.FullName, "", "")

asrc = sg.AddSource(WMENC_SOURCE_TYPE.WMENC_AUDIO)
asrc.SetInput(wmvFile.FullName, "", "")

# 下記を使用すると .prx とソースメディアファイルの
# エンコード設定が異なれば Profile の設定時に
# エラー（0xC00D1B5F）が発生
#
#sg.AutoSetFileSource(wmvFile.FullName)

enc.File.LocalFileName = FileInfo(sys.argv[2]).FullName

profile = WMEncProfile2Class()
profile.LoadFromFile(FileInfo(sys.argv[3]).FullName)

sg.Profile = profile

enc.PrepareToEncode(True)

enc.Start()

Console.WriteLine("encode Start")

while enc.RunState != WMENC_ENCODER_STATE.WMENC_ENCODER_STOPPED:
    Thread.Sleep(1000)

Console.WriteLine("encode End")
