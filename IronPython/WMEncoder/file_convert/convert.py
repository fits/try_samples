# coding: utf-8
#
# WMEncoder を使用してファイルを変換するサンプル
#

import sys

if len(sys.argv) < 3:
    print "convert.py [input file name] [output file name]"
    sys.exit()

from System import *
from System.IO import *
from System.Threading import *

import clr
clr.AddReference("Interop.WMEncoderLib")
from WMEncoderLib import *


def select_profile(profiles, name):

    result = None

    for pf in profiles:
        if pf.Name.StartsWith(name):
            result = pf
            break

    return result



app = WMEncoderAppClass()

enc = app.Encoder

sg = enc.SourceGroupCollection.Add("sg1")

src = sg.AddSource(WMENC_SOURCE_TYPE.WMENC_VIDEO)
src.SetInput(FileInfo(sys.argv[1]).FullName, "", "")

enc.File.LocalFileName = FileInfo(sys.argv[2]).FullName

profile = select_profile(enc.ProfileCollection, "Windows Media Video")

if profile:
    sg.Profile = profile

    enc.PrepareToEncode(True)

    enc.Start()

    Console.WriteLine("encode Start")

    while enc.RunState != WMENC_ENCODER_STATE.WMENC_ENCODER_STOPPED:
        Thread.Sleep(1000)

    Console.WriteLine("encode End")

