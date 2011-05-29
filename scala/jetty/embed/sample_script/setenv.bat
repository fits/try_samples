@echo off

for %%f in (lib\*) do call :add_cpath "%%f"

:add_cpath
  if "%CP%"=="" (
    set CP=%~1
  ) else (
    set CP=%CP%;%~1
  )
goto :eof

