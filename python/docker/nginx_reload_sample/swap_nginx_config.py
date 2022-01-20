import docker

import shutil
import sys
import os

name = sys.argv[1]
trg = sys.argv[2]

conf_file = './conf/default.conf'
new_conf_file = f'./conf/{trg}_conf'

if not os.path.isfile(new_conf_file):
    print(f'not found: {new_conf_file}')
    sys.exit(1)

client = docker.from_env()

container = client.containers.get(name)

if container.status != 'running':
    print(f'not running: {name}')
    sys.exit(1)

shutil.copy(new_conf_file, conf_file)

container.kill('HUP')

print('reloaded')
