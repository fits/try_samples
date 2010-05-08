@echo off

set S2_NET_HOME=c:\s2container.net-1.2.0-.net2.0
set ASPECTDNG_HOME=c:\aspectdng-0.9.95-bin

set OUTPUT_NAME=ExpressionAop.dll
set ADNG=aspectdng.exe

copy %S2_NET_HOME%\s2container.net\build\Seasar.dll Seasar.dll /Y
copy %S2_NET_HOME%\s2container.net\lib\log4net.dll log4net.dll /Y
copy %ASPECTDNG_HOME%\2.0\%ADNG% %ADNG% /Y

csc /out:%OUTPUT_NAME% /target:library /r:%ADNG%;Seasar.dll;Spring.Core.dll *Aspect.cs

%ADNG% Seasar.dll %OUTPUT_NAME%

csc /out:%OUTPUT_NAME% /t:library /r:%ADNG%;Seasar.dll;Spring.Core.dll *Aspect.cs

csc /r:Seasar.dll Tester.cs *Data*.cs
