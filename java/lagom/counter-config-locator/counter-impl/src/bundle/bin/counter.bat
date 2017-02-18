@echo off

set CONFIG=-Dhttp.address=%COUNTER_BIND_IP% -Dhttp.port=%COUNTER_BIND_PORT% -Dplay.crypto.secret=none

java -cp "../lib/*" %CONFIG% play.core.server.ProdServerStart
