@echo off

set BASE_DIR=%~d0%~p0
set LIB=%BASE_DIR%\lib

set CP=%BASE_DIR%;%BASE_DIR%\conf

for %%i in ("%LIB%\*.jar") do call %BASE_DIR%\lcp.bat %%i

java -cp %CP% fits.sample.App %*
