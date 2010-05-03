# coding: utf-8
#
# 音声+ビデオのプロファイルを音声だけに変更
#

import sys

if len(sys.argv) < 2:
    print "%s [profile file]" % sys.argv[0]
    sys.exit()

from System import *
from System.IO import *

import clr
clr.AddReference("Interop.WMEncoderLib")
from WMEncoderLib import *

profile = WMEncProfile2Class()
profile.LoadFromFile(FileInfo(sys.argv[1]).FullName)

#オーディエンスを確認（処理には無関係）
print profile.AudienceCount
aud = profile.Audience[0]
print aud.TotalBitrate

#音声のみサポート
profile.ContentType = 1

profile.SaveToFile(FileInfo("dest.prx").FullName)

