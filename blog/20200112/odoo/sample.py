
import xmlrpc.client

url = 'http://localhost:8069'

db = 'odoo1'
user = 'admin@example.com'
password = 'pass'

common = xmlrpc.client.ServerProxy(f'{url}/xmlrpc/2/common')

uid = common.authenticate(db, user, password, {})

print(f'uid: {uid}')

models = xmlrpc.client.ServerProxy(f'{url}/xmlrpc/2/object')

obj = 'product.product'

count = models.execute(db, uid, password, obj, 'search_count', [])

print(f'count: {count}')

kw = {'fields': ['name', 'lst_price', 'product_tmpl_id', 'qty_available']}

products = models.execute_kw(db, uid, password, obj, 'search_read', [], kw)

for p in products:
    print(p)
