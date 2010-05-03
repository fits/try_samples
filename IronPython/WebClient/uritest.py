# coding: utf-8
#
#Uri の動作確認

from System import *
from System.Net import *

def printUri(uri):
    print "scheme= %s, host= %s, port= %s, path= %s, segments= %s, authority= %s" % (uri.Scheme, uri.Host, uri.Port, uri.LocalPath, uri.Segments, uri.Authority)


printUri(Uri("http://localhost/a/b/index.php"))
printUri(Uri("http://localhost:8080/a/b/c"))
printUri(Uri("file:///c:/d"))
printUri(Uri("http://localhost"))

#エラーが発生
#uri = Uri("/a/b/c")
