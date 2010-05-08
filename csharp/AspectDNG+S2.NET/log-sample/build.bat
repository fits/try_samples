@echo off

set OUTPUT_NAME=Calc.exe
set ADNG=aspectdng.exe

csc /out:%OUTPUT_NAME% /r:%ADNG% *.cs

%ADNG% %OUTPUT_NAME%
