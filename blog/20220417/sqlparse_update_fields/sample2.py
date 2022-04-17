import sqlparse
from sqlparse.sql import Identifier, IdentifierList, Comparison, Token
from sqlparse.tokens import DML

import sys

sql = sys.stdin.read()

def fields_to_update(st):
    def process(ts, table = '', is_update = False):
        for t in ts.tokens:
            match t:
                case Token(ttype=tt, value=v) if tt == DML and v.upper() == 'UPDATE':
                    is_update = True
                case IdentifierList() if is_update:
                    yield from process(t, table, is_update)
                case Identifier() if is_update:
                    table = t.get_real_name()
                case Comparison(left=Identifier() as l) if is_update:
                    yield f"{table}.{l.get_real_name()}"
    
    yield from process(st)

for s in sqlparse.parse(sql):
    fields = list(fields_to_update(s))
    print(fields)
