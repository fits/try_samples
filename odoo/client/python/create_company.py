
import sys
import xmlrpc.client

url = 'http://localhost:8069'

db = sys.argv[1]
user = sys.argv[2]
password = sys.argv[3]

name = sys.argv[4]

common = xmlrpc.client.ServerProxy(f'{url}/xmlrpc/2/common')

uid = common.authenticate(db, user, password, {})

print(f'uid: {uid}')

models = xmlrpc.client.ServerProxy(f'{url}/xmlrpc/2/object')

obj = 'res.company'

ids = models.execute(db, uid, password, obj, 'create', [{'name': name}])

print(f'created: {ids}')

companies = models.execute(db, uid, password, obj, 'search_read', [])

for c in companies:
    print(f"id: {c['id']}, name: {c['name']}, currency_id: {c['currency_id']}, partner_id: {c['partner_id']}")
