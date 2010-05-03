# coding: utf-8
#
# プロファイルのチェック
#

import sys

if len(sys.argv) < 2:
    print "%s [profile file]" % sys.argv[0]
    sys.exit()

from System import *
from System.IO import *

#WMEncProfile2
profile = Activator.CreateInstance(Type.GetTypeFromCLSID(Guid("A5AC04E7-3E13-48CE-A43F-9FBA59DB1544")))
profile.LoadFromFile(FileInfo(sys.argv[1]).FullName)

ctype = profile.ContentType

print "is audio %s" % ((ctype & 1) == 1)
print "is video %s" % ((ctype & 16) == 16)
print "is script %s" % ((ctype & 256) == 256)
