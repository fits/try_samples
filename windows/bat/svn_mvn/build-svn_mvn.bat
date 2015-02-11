@echo off

set CURRENT_DIR=%~d0%~p0
set WORK_DIR=work

set REPO_URL=http://localhost/svn

set MVN_OPT=install -Dmaven.test.skip=true -s settings.xml

set MODULE_LIST=sample1 sample2 sample3

set SVN_CMD=jsvn
set MVN_CMD=mvn

for %%i in ( %MODULE_LIST% ) do (
	echo %%i

	if exist "%WORK_DIR%\%%i\.svn" (
		call %SVN_CMD% revert %WORK_DIR%\%%i
		call %SVN_CMD% update %WORK_DIR%\%%i
	) else (
		call %SVN_CMD% checkout %REPO_URL%/%%i %WORK_DIR%\%%i
	)

	cd %WORK_DIR%\%%i

	call %MVN_CMD% %MVN_OPT%

	cd %CURRENT_DIR%
)