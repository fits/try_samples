# coding: utf-8
#
# WMEncoder を使用してマルチビットレートのファイルを作成するサンプル
#

import sys

if len(sys.argv) < 3:
    print "multibitrate.py [input file name] [output file name]"
    sys.exit()

from System import *
from System.IO import *
from System.Threading import *

import clr
clr.AddReference("Interop.WMEncoderLib")
from WMEncoderLib import *


def create_profile():

    result = WMEncProfile2Class()
    result.ValidateMode = True

    result.ProfileName = "test profile"
    result.ContentType = 16

    #マルチビットレートを使用するためにモードを CBR に変更する
    result.VBRMode[WMENC_SOURCE_TYPE.WMENC_VIDEO, 0] = WMENC_PROFILE_VBR_MODE.WMENC_PVM_NONE

    result.AddAudience(200000)
    result.AddAudience(1000000)

    aud1 = result.Audience[0]

    #マルチビットレートの場合はコーデックを共通化する必要有り
#   aud1.VideoCodec[0] = 2
#   aud1.VideoHeight[0] = 0
#   aud1.VideoWidth[0] = 0
    aud1.VideoBufferSize[0] = 10000
    aud1.VideoImageSharpness[0] = 30

    result.Validate()

    result.SaveToFile(FileInfo("temp.prx").FullName)

    return result


app = WMEncoderAppClass()

enc = app.Encoder

sg = enc.SourceGroupCollection.Add("sg1")

src = sg.AddSource(WMENC_SOURCE_TYPE.WMENC_VIDEO)
src.SetInput(FileInfo(sys.argv[1]).FullName, "", "")

enc.File.LocalFileName = FileInfo(sys.argv[2]).FullName

profile = create_profile()

if profile:
    sg.Profile = profile

    enc.PrepareToEncode(True)

    enc.Start()

    Console.WriteLine("encode Start")

    while enc.RunState != WMENC_ENCODER_STATE.WMENC_ENCODER_STOPPED:
        Thread.Sleep(1000)

    Console.WriteLine("encode End")

