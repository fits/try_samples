# coding: utf-8
#
# VirtualBox の仮想マシンを export するサンプル

import sys

if len(sys.argv) < 3:
	print "%s <machine name> <output file>" % sys.argv[0]
	sys.exit()

import os

import clr
clr.AddReference("VirtualBox")

from VirtualBox import *

vb = VirtualBoxClass()

# 仮想マシンの取得
mc = vb.FindMachine(sys.argv[1])

fileName = sys.argv[2]
f, ext = os.path.splitext(fileName)

ap = vb.CreateAppliance()

# 第2引数の値が xxx-disk1.vmdk の xxx に該当
des = mc.Export(ap, f)

print "export start"

pr = ap.Write("ovf-2.0", 0, os.path.join(os.getcwd(), fileName))

# 処理が完了するまで待機
pr.WaitForCompletion(-1)

print "export end"
