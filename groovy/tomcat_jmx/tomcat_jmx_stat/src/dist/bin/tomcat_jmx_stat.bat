@echo off

setlocal enabledelayedexpansion

set APP_CLASS=JmxStat

set BASE_DIR=%~d0%~p0

set CP=%BASE_DIR%;%JAVA_HOME%\lib\tools.jar
set LIB=%BASE_DIR%..\lib

for %%j in ("%LIB%\*.jar") do call set CP=!CP!;%%j

java -cp %CP% %APP_CLASS% %*

endlocal
