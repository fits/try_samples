
import json

fw = open('sample.json', 'w')

obj = ['a', {'d': 123}]

json.dump(obj, fw)

fw.close()

fr = open('sample.json', 'r')

print(json.load(fr))

fr.close()
