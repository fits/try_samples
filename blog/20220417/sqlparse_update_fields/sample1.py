import sqlparse
from sqlparse.sql import Identifier, IdentifierList, Comparison, Token
from sqlparse.tokens import DML

import sys

sql = sys.stdin.read()

def fields_to_update(st):
    is_update = False
    table = ''

    for t in st.tokens:
        match t:
            case Token(ttype=tt, value=v) if tt == DML and v.upper() == 'UPDATE':
                is_update = True
            case IdentifierList() if is_update:
                for c in t.tokens:
                    match c:
                        case Comparison(left=Identifier() as l) if is_update:
                            yield f"{table}.{l.get_real_name()}"
            case Identifier() if is_update:
                table = t.get_real_name()
            case Comparison(left=Identifier() as l) if is_update:
                yield f"{table}.{l.get_real_name()}"

for s in sqlparse.parse(sql):
    fields = list(fields_to_update(s))
    print(fields)
