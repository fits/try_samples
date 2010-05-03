# coding: utf-8
#
# WMEncoder を使用してサーバーに動画をプッシュするサンプル
#
# WMS 側の「WMS公開ポイントACL承認」で Everyone に
# 書き込み・作成権限を付与しておく必要有り
#

import sys

if len(sys.argv) < 2:
    print "record.py [server name]"
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

def exec_credential(realm, site, user, passwd, flags):
    print ("realm:%s, site:%s" % realm, site)
    
    user = "test"
    passwd = "testpass"
    
    return (user, passwd, flags)

app = WMEncoderAppClass()

enc = app.Encoder

#app.Encoder を WMEncoder にキャストする方法が不明なためコメントアウト
#enc.OnAcquireCredentials += exec_credential

sg = enc.SourceGroupCollection.Add("sg1")

src = sg.AddSource(WMENC_SOURCE_TYPE.WMENC_VIDEO)
src.SetInput("Default_Video_Device", "Device", "")

profile = select_profile(enc.ProfileCollection, "Windows Media Video")

pushDist = enc.Broadcast

pushDist.AutoRemovePublishingPoint = True

pushDist.ServerName = sys.argv[1]
pushDist.PublishingPoint = "TestPublish"
#新規公開ポイントを作るためのテンプレートの指定（既に存在する公開ポイント指定）
pushDist.Template = "test"

if profile:
    sg.Profile = profile

    enc.PrepareToEncode(True)

    enc.Start()

    Console.WriteLine("start record")

    Console.ReadLine()

    enc.Stop()

