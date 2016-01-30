@echo off

setlocal enabledelayedexpansion

set DEST_DIR=%1

for %%f in (%2) do (

	set DEST_FILE=%DEST_DIR%/%%~nf%%~xf

	convert %%f -type GrayScale -negate -edge 2 -negate -threshold 50%% -despeckle !DEST_FILE!.pnm

	potrace !DEST_FILE!.pnm -s -o !DEST_FILE!.svg

	echo done: !DEST_FILE!.svg
)
