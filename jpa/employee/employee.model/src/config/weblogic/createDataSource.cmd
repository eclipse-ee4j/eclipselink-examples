@echo off

set LOC=%1%

if /I "%LOC%"=="" (goto error)
goto start

:start
call "%LOC%\server\bin\setWLSEnv.cmd"
call "%JAVA_HOME%\bin\java" weblogic.WLST createDataSource.py"

goto end

:error
echo "No arguments supplied: Must provide directory where WLS is installed"
echo " USAGE: createDataSource.cmd C:\Oracle\Middleware\wlserver_12.1"

:end
@endlocal