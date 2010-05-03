# coding: utf-8
#
# WMEncoder を使用してローカルファイルに動画を録画するサンプル
#

import sys

if len(sys.argv) < 2:
    print "record.py [output file name]"
    sys.exit()

from System import *
from System.IO import *
from System.Runtime.InteropServices import *

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
src.SetInput("Default_Video_Device", "Device", "")

enc.File.LocalFileName = FileInfo(sys.argv[1]).FullName

profile = select_profile(enc.ProfileCollection, "Windows Media Video")

if profile:
    sg.Profile = profile

    enc.PrepareToEncode(True)

    enc.Start()

    Console.ReadLine()

    enc.Stop()

