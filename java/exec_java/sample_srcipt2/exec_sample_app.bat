@echo off

setlocal

set APP_CLASS=SampleApp

call %~d0%~p0exec_java %*

endlocal
