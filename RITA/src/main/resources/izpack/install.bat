@echo off
if "%OS%"=="WINNT" goto :javawin
if "%OS%"=="Windows_NT" goto :javawin

java -jar RitaSetup.jar ${PWD##*/}
goto :end

:javawin
cmd.exe /C java -jar RitaSetup.jar

:end