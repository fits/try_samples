
from openpyxl import Workbook

wb = Workbook()

ws1 = wb.create_sheet('test', 0)

ws1['A1'] = 'sample'
ws1['B1'] = 10

ws1.cell(row = 2, column = 3, value = 'abc')

ws1.append(['item1', 1])

print(ws1.max_row)

print( list(ws1.rows) )

print( list(ws1.values) )

wb.save('sample.xlsx')