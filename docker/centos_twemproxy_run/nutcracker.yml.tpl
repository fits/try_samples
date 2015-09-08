sample:
  listen: 0.0.0.0:6379
  hash: fnv1a_64
  distribution: ketama
  auto_eject_hosts: true
  timeout: 400
  redis: true
  servers:
#{SERVERS}
