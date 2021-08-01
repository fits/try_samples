
import sys
from sql_metadata import Parser

sql = sys.argv[1]

r = Parser(sql).tables

print(r)
