# coding: utf-8
#
# VirtualBox の仮想マシンのリネームを行うサンプル

import sys

if len(sys.argv) < 3:
	print "%s <machine name> <new name>" % sys.argv[0]
	sys.exit()

import clr
clr.AddReference("VirtualBox")

from VirtualBox import *

vb = VirtualBoxClass()

# 仮想マシンの取得
mc = vb.FindMachine(sys.argv[1])

print "current name : %s" % mc.Name

session = SessionClass()
# 設定変更用にロックする
mc.LockMachine(session, LockType.LockType_Write)

# 変更用の仮想マシンを取得
upMc = session.Machine

upMc.Name = sys.argv[2]

try:
	# 変更の適用
	upMc.SaveSettings()

	print "new name : %s" % mc.Name

finally:
	# ロックを解除する
	session.UnlockMachine()

