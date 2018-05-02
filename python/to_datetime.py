from datetime import datetime as dt

to_datetime = lambda s: dt.strptime(s, '%Y-%m-%d')

print( to_datetime('2018-04-29') )
