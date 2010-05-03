# coding: utf-8
#
# WMEncoder を使用してストリームの一部を取り出すサンプル
#

import sys

if len(sys.argv) < 2:
    print "split.py [input file name] [split count]"
    sys.exit()

from System import *
from System.IO import *
from System.Threading import *
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

#
def get_duration(inputFile):

    edit = WMEncBasicEditClass()
    edit.MediaFile = inputFile.FullName

    result = edit.Duration

    Marshal.ReleaseComObject(edit)

    return result

def set_Mark(sourceGroup, start, end):

    for sourceType in [WMENC_SOURCE_TYPE.WMENC_AUDIO, WMENC_SOURCE_TYPE.WMENC_VIDEO]:
        if sourceGroup.SourceCount[sourceType] > 0:
            src = sourceGroup.Source[sourceType, 0]
            src.MarkIn = start
            src.MarkOut = end



file = FileInfo(sys.argv[1])

duration = get_duration(file)

app = WMEncoderAppClass()

enc = app.Encoder

sgcol = enc.SourceGroupCollection

if len(sys.argv) > 2:
    num = int(sys.argv[2])
else:
    num = 3

profile = select_profile(enc.ProfileCollection, "Windows Media Video")

print "duration : %s" % duration

for i in range(0, num):

    if sgcol.Count > 0:
        sgcol.Remove("sg1")

    sg = sgcol.Add("sg1")

    sg.AutoSetFileSource(file.FullName)

    start = (duration * i) / num
    end = (duration * (i + 1) / num) - (duration / num / 5)

    print "start : %s, end: %s" % (start, end)

    set_Mark(sg, start, end)

    if profile:
        sg.Profile = profile

        enc.File.LocalFileName = FileInfo("dest%d.wmv" % i).FullName

        enc.PrepareToEncode(True)

        enc.Start()

        print "encode Start %s" % i

        while enc.RunState != WMENC_ENCODER_STATE.WMENC_ENCODER_STOPPED:
            Thread.Sleep(1000)

        print "encode End %s" % i

print "All End"
Console.ReadLine()
