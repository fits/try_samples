@echo off

set BASE_DIR=%~d0%~p0

java -cp %JAVA_HOME%/lib/tools.jar;%BASE_DIR%/thread_stat.jar sample.ThreadStat %*
