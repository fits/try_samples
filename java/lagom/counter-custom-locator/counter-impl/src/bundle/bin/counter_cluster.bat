@echo off

set CONFIG=-Dhttp.address=%COUNTER_BIND_IP% -Dhttp.port=%COUNTER_BIND_HTTP_PORT% -Dplay.crypto.secret=none 

set CLUSTER_CONFIG=-Dakka.remote.netty.tcp.hostname=%COUNTER_BIND_IP% -Dakka.remote.netty.tcp.port=%COUNTER_BIND_AKKA_PORT% -Dakka.cluster.seed-nodes.0=akka.tcp://application@%CLUSTER_SEED_HOST%:%CLUSTER_SEED_PORT%

java -cp "../lib/*" %CONFIG% %CLUSTER_CONFIG% play.core.server.ProdServerStart
