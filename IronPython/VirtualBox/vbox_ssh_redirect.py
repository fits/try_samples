# coding: utf-8
#
# VirtualBox の仮想マシンへ SSH 用のポートフォワード設定を追加するサンプル

import sys

if len(sys.argv) < 2:
	print "%s <machine name>" % sys.argv[0]
	sys.exit()

import os

import clr
clr.AddReference("VirtualBox")

from System import *
from VirtualBox import *

vb = VirtualBoxClass()

# 仮想マシンの取得
mc = vb.FindMachine(sys.argv[1])

# ポートフォワードの設定追加
na = mc.GetNetworkAdapter(0)
na.NATEngine.addRedirect("ssh-forward", NATProtocol.NATProtocol_TCP, "", 2222, "", 22)
