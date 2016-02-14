@echo off

setlocal enabledelayedexpansion

set DEST_DIR=%1

for %%f in (%2) do (

	set DEST_FILE=%DEST_DIR%/%%~nf%%~xf

	node line_chart.js %%f > !DEST_FILE!.svg

	echo done: !DEST_FILE!.svg

	phantomjs web_capture.js !DEST_FILE!.svg !DEST_FILE!.png

	echo done: !DEST_FILE!.png
)
