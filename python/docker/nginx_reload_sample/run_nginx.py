import docker

import sys
from pathlib import Path

conf_dir = Path('./conf').resolve()

name = sys.argv[1]
port = int(sys.argv[2]) if len(sys.argv) > 2 else 8080

client = docker.from_env()

try:
    client.containers.get(name).remove(force=True)
    print(f'removed: {name}')
except docker.errors.NotFound:
    pass

container = client.containers.run(
    image='nginx',
    name=name,
    ports={'8080/tcp': port},
    volumes=[f'{conf_dir}:/etc/nginx/conf.d'],
    detach=True
)

print(f'name={container.name}, status={container.status}')
print(client.containers.get(container.name).status)
