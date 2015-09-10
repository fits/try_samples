sample:
  listen: 0.0.0.0:6379
  hash: fnv1a_64
  distribution: ketama
  auto_eject_hosts: true
  server_retry_timeout: 2000
  server_failure_limit: 1
  timeout: 400
  redis: true
  servers:
#{SERVERS}
