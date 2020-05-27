@echo off

set /P ORACLE_USER=Oracle database username: 

set /P ORACLE_PASS=Oracle database password: 

call .\gradlew.bat -Dlogin=%ORACLE_USER% -Dpassword=%ORACLE_PASS% run
