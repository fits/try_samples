
import json

obj = ['a', {'d': 123}]

str = json.dumps(obj)

print(str)

dobj = json.loads(str)

print(dobj[0])
print(dobj[1])
print(dobj[1]['d'])
