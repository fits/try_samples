
import configparser
import os

config = configparser.ConfigParser()

conf_file = os.environ.get('CONF_FILE', 'sample.ini')

config.read(conf_file)

if 'sample' in config:
    name = config['sample']['name']
    value = config['sample']['value']

    print(f"name = {name}, value = {value}")
else:
    config['sample'] = { 'name': 'test-1', 'value': 123 }

    with open(conf_file, 'w') as f:
        config.write(f)
