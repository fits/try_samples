import docker
import sys

name = sys.argv[1]

client = docker.from_env()

try:
    c = client.containers.get(name)
    print(f'id={c.short_id}, name={c.name}, image={c.image.tags}, status={c.status}')
except docker.errors.NotFound as e:
    print(e)
