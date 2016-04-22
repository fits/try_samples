@echo off

for /l %%i in (1, 1, %1) do timeout /T %2 /NOBREAK && echo run %%i
