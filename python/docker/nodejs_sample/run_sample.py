import docker
from pathlib import Path

scripts_path = Path('./scripts').resolve()

client = docker.from_env()

res = client.containers.run(
    image='node', 
    command='node index.js',
    volumes=[f'{scripts_path}:/work'],
    working_dir='/work',
    remove=True
)

print(res)

try:
    client.containers.run(
        image='node', 
        command='node index2.js',
        volumes=[f'{scripts_path}:/work'],
        working_dir='/work',
        remove=True,
        stderr=True
    )
except Exception as e:
    print(e)
