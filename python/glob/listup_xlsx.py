# coding: utf-8
#
import sys
import glob

for f in glob.glob("%s/*.xlsx" % sys.argv[1]):
	print f
