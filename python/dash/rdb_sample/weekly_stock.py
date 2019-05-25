# -*- coding: utf-8 -*-
import records
import pandas as pd
import dash
import dash_table
import dash_core_components as dcc
import dash_html_components as html

db = records.Database()

rows = db.query_file('weekly_stock.sql').as_dict()

df = pd.DataFrame.from_dict(rows)
df['qty'] = df['qty'].astype('int32')

pt = pd.pivot_table(df, index = ['product_id', 'product_name'], columns = 'week', values = 'qty').fillna(0)

app = dash.Dash(__name__)

app.layout = html.Div(children = [
    html.H1(children = 'weekly stocks'),
    dash_table.DataTable(
        id = 'table1',
        columns = 
            [{'id': 'product_name', 'name': 'product'}] + 
            [{'id': c} for c in pt.columns.values]
        ,
        data = pt.reset_index().to_dict(orient = 'records'),
        style_cell_conditional = [
            {'if': {'column_id': c}, 'textAlign': 'left'} 
            for c in ['product_name']
        ],
        sorting = True,
        filtering = True
    )
])

app.run_server(debug = True)
