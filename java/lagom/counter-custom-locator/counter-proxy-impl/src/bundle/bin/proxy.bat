@echo off

set CONFIG=-Dhttp.address=%PROXY_BIND_IP% -Dhttp.port=%PROXY_BIND_PORT% -Dplay.crypto.secret=none

java -cp "../lib/*" %CONFIG% play.core.server.ProdServerStart
