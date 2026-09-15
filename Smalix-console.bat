@echo off
setlocal
cd /d "%~dp0"

rem === Smalix launcher (by DedSec - dedseec.com) ===
rem Java dhundhta hai: JAVA_HOME -> PATH -> common install folders

set "JW="
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "JW=%JAVA_HOME%\bin\java.exe"
if not defined JW for %%J in (java.exe) do if exist "%%~$PATH:J" set "JW=%%~$PATH:J"

if not defined JW call :find "%ProgramFiles%\Eclipse Adoptium"
if not defined JW call :find "%ProgramFiles%\Java"
if not defined JW call :find "%ProgramFiles%\Microsoft"
if not defined JW call :find "%ProgramFiles%\Zulu"
if not defined JW call :find "%ProgramFiles%\Android\Android Studio\jbr"
if not defined JW call :find "%ProgramFiles(x86)%\Java"

if not defined JW (
  echo.
  echo   Java nahi mila. Java 17+ install karein: https://adoptium.net
  echo.
  pause
  exit /b 1
)

"%JW%" -jar "%~dp0Smalix.jar"
pause
exit /b 0

:find
if defined JW goto :eof
for /f "delims=" %%D in ('dir /b /s "%~1\java.exe" 2^>nul') do (
  set "JW=%%D"
  goto :eof
)
goto :eof
