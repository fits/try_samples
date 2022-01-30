import docker

import sys
from pathlib import Path

solr_name = sys.argv[1]
#solr_port = int(sys.argv[2]) if len(sys.argv) > 2 else 8983
solr_core = 'core1'

nginx_name = 'solr_nginx1'
nginx_port = 8080

network_name = 'solr_test'

nginx_conf_path = Path('./nginx_conf').resolve()
solr_conf_path = Path('./solr_conf').resolve()
scripts_path = Path('./scripts').resolve()

nginx_default_conf = '''
server {
    listen 8080;

    location / {
        return 200 "default";
    }
}
'''.strip()

def create_nginx_conf(s):
    fs = open(f'{nginx_conf_path}/default.conf', 'w')
    fs.write(s)
    fs.close()

client = docker.from_env()

try:
    client.networks.get(network_name)
except docker.errors.NotFound:
    client.networks.create(network_name, driver='bridge')

try:
    nginx = client.containers.get(nginx_name)
except docker.errors.NotFound:
    create_nginx_conf(nginx_default_conf)

    nginx = client.containers.run(
        image='nginx',
        name=nginx_name,
        network=network_name,
        ports={'8080/tcp': nginx_port},
        volumes=[f'{nginx_conf_path}:/etc/nginx/conf.d'],
        detach=True
    )

print(f'nginx: name={nginx.name}, status={nginx.status}')

try:
    client.containers.get(solr_name).remove(force=True)
    print(f'rmoved {solr_name}')
except docker.errors.NotFound:
    pass

solr = client.containers.run(
    image='solr',
    command=f'solr-precreate {solr_core} /solr_config',
    name=solr_name,
    network=network_name,
    #ports={'8983/tcp': solr_port},
    volumes=[f'{solr_conf_path}:/solr_config/conf'],
    detach=True
)

print(f'solr: name={solr.name}, status={solr.status}')

r = client.containers.run(
    image='denoland/deno',
    command='run --allow-env --allow-net init.ts',
    network=network_name,
    volumes=[f'{scripts_path}:/work'],
    working_dir='/work',
    environment={
        'SOLR_URL': f'http://{solr.name}:8983/solr/{solr_core}'
    },
    stderr=True,
    remove=True
)

print(r)

create_nginx_conf('''
server {{
    listen 8080;

    location / {{
        proxy_pass http://{solr_name}:8983/;
    }}
}}
'''.format(solr_name=solr.name).strip())

nginx.kill('HUP')

print('reloaded nginx')
