# coding: utf-8
#
# VirtualBox の仮想マシンをリストアップするサンプル

from System import *
from System.IO import *

import clr
clr.AddReference("VirtualBox")

from VirtualBox import *

vb = VirtualBoxClass()

for m in vb.Machines:
	print "%s: %s" % (m.Id, m.Name)
