
import sys
import xmlrpc.client

url = 'http://localhost:8069'

db = sys.argv[1]
user = sys.argv[2]
password = sys.argv[3]

common = xmlrpc.client.ServerProxy(f'{url}/xmlrpc/2/common')

print(common.version())

uid = common.authenticate(db, user, password, {})

print(f'uid: {uid}')

models = xmlrpc.client.ServerProxy(f'{url}/xmlrpc/2/object')

obj = 'product.product'

count = models.execute(db, uid, password, obj, 'search_count', [])

print(f'count: {count}')

kw = {'fields': ['name', 'price', 'default_code']}

products = models.execute_kw(db, uid, password, obj, 'search_read', [], kw)

for p in products:
    print(f"id: {p['id']}, name: {p['name']}, price: {p['price']}, default_code: {p['default_code']}")
