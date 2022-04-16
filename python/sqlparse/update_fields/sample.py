import sqlparse
from sqlparse.sql import Identifier, IdentifierList, Comparison, Token
from sqlparse.tokens import DML, DDL

import sys

sql = sys.stdin.read()

def fields_to_update(st, prefix=[]):
    p = prefix

    for t in st.tokens:
        match t:
            case Token(ttype=tt, value=v) if tt == DDL or (tt == DML and v.upper() != 'UPDATE'):
                break
            case IdentifierList():
                yield from fields_to_update(t, p)
            case Identifier():
                p = p + [t.get_real_name()]
            case Comparison(left=Identifier() as l):
                yield '.'.join(p + [l.get_real_name()])

for s in sqlparse.parse(sql):
    fields = list(fields_to_update(s))
    print(fields)

    print('-----')
