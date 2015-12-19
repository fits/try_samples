@echo off

set NODE_PATH=output
node -e "require('Main/index.js').main()"
