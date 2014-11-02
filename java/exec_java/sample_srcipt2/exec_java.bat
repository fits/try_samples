@echo off

setlocal enabledelayedexpansion

set JAVA_CMD=java

set BASE_DIR=%~d0%~p0

set CP=%BASE_DIR%
set LIB=%BASE_DIR%lib

for %%i in ("%LIB%\*.jar") do call set CP=!CP!;%%i

%JAVA_CMD% -cp %CP% %APP_CLASS% %*

endlocal
