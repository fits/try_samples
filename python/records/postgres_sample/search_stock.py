# -*- coding: utf-8 -*-
import records

db = records.Database()

sql = '''
    select
        sm.product_id,
        pt.name as product_name,
        date_trunc('week', sm.date) as week,
        sum(sm.product_uom_qty) as qty
    from
        stock_move sm, 
        product_product pp, 
        product_template pt
    where
        sm.state = 'done' and
        sm.product_id = pp.id and 
        pp.product_tmpl_id = pt.id
    group by
        sm.product_id, product_name, week
    order by
        product_name, week
'''

for r in db.query(sql):
    print(f'product: {r.product_name}, week: {r.week}, qty: {r.qty}')
