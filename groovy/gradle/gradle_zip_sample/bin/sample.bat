@echo off

set BASE_DIR=%~d0%~p0

set CLASSPATH=%BASE_DIR%\.;
set LIB=%BASE_DIR%\lib

for %%i in ("%LIB%\*.jar") do call %BASE_DIR%\cpappend.bat %%i

java -cp %CLASSPATH% fits.sample.SampleApp %*
