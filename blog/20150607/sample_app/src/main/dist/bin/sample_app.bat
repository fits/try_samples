@echo off

setlocal enabledelayedexpansion

set APP_CLASS=App

set BASE_DIR=%~d0%~p0

set CP=%BASE_DIR%
set LIB=%BASE_DIR%..\lib

for %%j in ("%LIB%\*.jar") do call set CP=!CP!;%%j

java -cp %CP% %APP_CLASS% %*

endlocal
