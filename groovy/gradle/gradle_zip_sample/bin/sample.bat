@echo off

set BASE_DIR=%~d0%~p0

set CP=%BASE_DIR%\.;
set LIB=%BASE_DIR%\lib

for %%i in ("%LIB%\*.jar") do call %BASE_DIR%\lcp.bat %%i

java -cp %CP% fits.sample.SampleApp %*
