# coding: utf-8
#
# .NET の API を使って文字列の一部を変換するサンプル

import clr
from System import *
from System.IO import *
from System.Text.RegularExpressions import *

data = File.ReadAllText("data.txt")

regex = Regex("//@@@StartPos.*?//@@@EndPos", RegexOptions.Singleline)

data_new = regex.Replace(data, "//@@@StartPos\ntest\n//@@@EndPos")

print data_new
