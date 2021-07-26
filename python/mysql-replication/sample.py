
from pymysqlreplication import BinLogStreamReader
from pymysqlreplication.row_event import RowsEvent

cfg = {'host': '127.0.0.1', 'port': 3306, 'user': 'root', 'password': ''}

stream = BinLogStreamReader(connection_settings = cfg, server_id = 1)

for ev in stream:
    ev.dump()

    if isinstance(ev, RowsEvent):
        print(f"rows={ev.rows}")

stream.close()
