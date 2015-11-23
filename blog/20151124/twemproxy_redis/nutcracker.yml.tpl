sample:
  listen: 0.0.0.0:6379
  hash: fnv1a_64
  distribution: ketama
  timeout: 500
  redis: true
  servers:
#{SERVERS}
