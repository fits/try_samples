
import sys
import xmlrpc.client

url = 'http://localhost:8069'

db = sys.argv[1]
user = sys.argv[2]
password = sys.argv[3]

name = sys.argv[4]
value = int(sys.argv[5])

common = xmlrpc.client.ServerProxy(f'{url}/xmlrpc/2/common')

print(common.version())

uid = common.authenticate(db, user, password, {})

print(f'uid: {uid}')

models = xmlrpc.client.ServerProxy(f'{url}/xmlrpc/2/object')

obj = 'sample.item'

count = models.execute(db, uid, password, obj, 'create', [{'name': name, 'value': value}])

items = models.execute(db, uid, password, obj, 'search_read', [])

for t in items:
    print(t)
