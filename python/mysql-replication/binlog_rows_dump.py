
import configparser
from datetime import date, datetime
import json
import os
import sys
import signal

from pymysqlreplication import BinLogStreamReader
from pymysqlreplication.row_event import (WriteRowsEvent, UpdateRowsEvent, DeleteRowsEvent)

class BinlogConfig:
    def __init__(self, conf_file):
        self.config = configparser.ConfigParser()
        self.conf_file = conf_file

    def load(self):
        self.config.read(self.conf_file)

        if 'binlog' in self.config:
            return (
                self.config['binlog']['log_file'], 
                int(self.config['binlog']['log_pos'])
            )

        return (None, None)

    def save(self, log_file, log_pos):
        self.config['binlog'] = {
            'log_file': stream.log_file, 
            'log_pos': stream.log_pos
        }

        with open(self.conf_file, 'w') as f:
            self.config.write(f)

def to_bool(s):
    return s.lower() in ['true', 't', 'ok', 'yes', 'y', 'on', '1']

def split_env(name):
    v = os.getenv(name)

    if v is None:
        return None

    return v.split(',')

ini_file = os.getenv('INI_FILE', 'binlog.ini')

bconf = BinlogConfig(ini_file)
(log_file, log_pos) = bconf.load()

blocking = to_bool(os.getenv('BLOCKING', 'off'))

host = os.getenv('MYSQL_HOST', 'localhost')
port = int(os.getenv('MYSQL_PORT', '3306'))
user = os.getenv('MYSQL_USER')
password = os.getenv('MYSQL_PASSWORD')

schemas = split_env('SCHEMAS')
tables = split_env('TABLES')

cfg = {'host': host, 'port': port, 'user': user, 'password': password}

def to_json(obj):
    if isinstance(obj, (datetime, date)):
        return obj.isoformat()
    return str(obj)

def handle_signal(sig, frame):
    sys.exit(1)

stream = BinLogStreamReader(
    connection_settings = cfg, 
    server_id = 1, 
    only_events = [WriteRowsEvent, UpdateRowsEvent, DeleteRowsEvent],
    only_schemas = schemas,
    only_tables = tables,
    resume_stream = True,
    log_file = log_file,
    log_pos = log_pos,
    blocking = blocking
)

try:
    signal.signal(signal.SIGTERM, handle_signal)

    for ev in stream:
        for r in ev.rows:
            data = {'table': '', 'schema': '', 'event_type': ''}

            if 'values' in r:
                data.update(r['values'])
            if 'after_values' in r:
                data.update(r['after_values'])

            data['table'] = ev.table
            data['schema'] = ev.schema

            if isinstance(ev, WriteRowsEvent):
                data['event_type'] = 'insert'
            elif isinstance(ev, UpdateRowsEvent):
                data['event_type'] = 'update'
            elif isinstance(ev, DeleteRowsEvent):
                data['event_type'] = 'delete'

            print( json.dumps(data, default=to_json) )
finally:
    signal.signal(signal.SIGTERM, signal.SIG_IGN)
    signal.signal(signal.SIGINT, signal.SIG_IGN)

    stream.close()
    bconf.save(stream.log_file, stream.log_pos)

    signal.signal(signal.SIGTERM, signal.SIG_DFL)
    signal.signal(signal.SIGINT, signal.SIG_DFL)
