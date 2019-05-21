# -*- coding: utf-8 -*-
import dash
import dash_core_components as dcc
import dash_html_components as html

app = dash.Dash(__name__)

app.layout = html.Div(children = [
    html.H3(children = 'Sample'),
    dcc.Graph(
        id = 'graph1',
        figure = {
            'data': [
                {'x': [1, 2, 3, 4, 5], 'y': [3, 5, 4, 2, 6], 'type': 'bar', 'name': 'data1'},
                {'x': [1, 2, 3, 4, 5], 'y': [5, 1, 1, 5, 3], 'type': 'bar', 'name': 'data2'},
                {'x': [1, 2, 3, 4, 5], 'y': [1, 2, 1, 3, 4], 'type': 'bar', 'name': 'data3'}
            ],
            'layout': {
                'title': 'sample graph1'
            }
        }
    )
])

app.run_server(debug = True)
