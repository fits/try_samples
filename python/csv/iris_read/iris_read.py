
import sys
import csv
import random

lines = list(csv.reader(open(sys.argv[1], 'r')))

values = lines[1:]

random.shuffle(values)

for v in values:
	print(v)
