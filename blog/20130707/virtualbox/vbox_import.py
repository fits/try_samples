# coding: utf-8
#
# VirtualBox の仮想マシンを import するサンプル

import sys

if len(sys.argv) < 2:
	print "%s <input file>" % sys.argv[0]
	sys.exit()

import clr
clr.AddReference("VirtualBox")

from System import *
from VirtualBox import *

vb = VirtualBoxClass()

ap = vb.CreateAppliance()

print "import start"

pr = ap.read(sys.argv[1])

pr.WaitForCompletion(-1)

ap.interpret()

opts = Array.CreateInstance(Int32, 0)

pr2 = ap.importMachines(opts)

pr2.WaitForCompletion(-1)

print "import end"
