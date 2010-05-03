# coding: shift_jis

import re

print "テスト"

msg = "てすとでーたabcちぇっく"

m = re.search("([abc]+)", msg)

print m.group()

