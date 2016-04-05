@echo off

set MODS_LIB_DIR=mods-lib

if not exist "%MODS_LIB_DIR%" mkdir %MODS_LIB_DIR%

jmod create --class-path mods %MODS_LIB_DIR%/jp.sample.jmod

jlink --modulepath %JAVA_HOME%/jmods;%MODS_LIB_DIR% --addmods jp.sample --output sample_java
