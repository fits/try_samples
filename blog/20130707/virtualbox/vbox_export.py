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

filePath = sys.argv[2]
fileName = os.path.basename(filePath)
fileNameBase, ext = os.path.splitext(fileName)

# (1) 空の IAppliance 作成
ap = vb.CreateAppliance()

# (2) Export 実施
# 第2引数の値が xxx-disk1.vmdk の xxx に該当（下記では <fileNameBase>-disk1.vmdk）
des = mc.Export(ap, fileNameBase)

print "export start"

# (3) ファイルへの出力
pr = ap.Write("ovf-2.0", 0, filePath)

# 処理が完了するまで待機
pr.WaitForCompletion(-1)

print "export end"
