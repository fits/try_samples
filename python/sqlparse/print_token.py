
import sys
import sqlparse

sql = sys.argv[1]

r = sqlparse.parse(sql)[0]

print(r)

print('-----')

for t in r.tokens:
    if isinstance(t, sqlparse.sql.Token):
        print(t)
