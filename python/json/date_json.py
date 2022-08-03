from datetime import datetime, date
import json

d1 = { 'name': 'sample1', 'date': datetime.now() }
d2 = { 'name': 'sample2', 'date': date.today() }

try:
    print( json.dumps(d1) )
except:
    print('error')

try:
    print( json.dumps(d2) )
except:
    print('error')

print( json.dumps(d1, default=str) )
print( json.dumps(d2, default=str) )

def to_json(obj):
    if isinstance(obj, (datetime, date)):
        return obj.isoformat()
    return str(obj)

print( json.dumps(d1, default=to_json) )
print( json.dumps(d2, default=to_json) )
