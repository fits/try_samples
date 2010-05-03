# coding: utf-8
#
#WebClient のプロキシ設定確認用サンプル

from System import *
from System.Net import *

wc = WebClient()
wc.BaseAddress = "http://www.google.co.jp/"

print wc.Proxy.GetProxy(Uri("http://www.google.co.jp/"))

print wc.DownloadString("")
