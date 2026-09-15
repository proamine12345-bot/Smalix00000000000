@echo off
REM ---------------------------------------------------------------------------
REM  Smalix engine downloader (Windows)
REM  Fetches the large engine jars from the GitHub Release into bin\.
REM  These are NOT stored in git (jadx-all.jar alone is >100MB).
REM ---------------------------------------------------------------------------
setlocal

REM EDIT THESE two lines after you create your repo + release:
set "REPO=USERNAME/Smalix"
set "TAG=engines-v3.0"

set "BASE=https://github.com/%REPO%/releases/download/%TAG%"
set "BIN=%~dp0..\bin"
if not exist "%BIN%\dex2jar" mkdir "%BIN%\dex2jar"

echo Downloading Smalix engines from %REPO%@%TAG% ...

call :get "%BASE%/jadx-all.jar"        "%BIN%\jadx-all.jar"
call :get "%BASE%/apktool.jar"         "%BIN%\apktool.jar"
call :get "%BASE%/uber-apk-signer.jar" "%BIN%\uber-apk-signer.jar"

if not exist "%BIN%\dex2jar\dex-tools-v2.4.jar" (
  echo   Downloading dex2jar.zip
  curl -fL "%BASE%/dex2jar.zip" -o "%BIN%\dex2jar.zip"
  tar -xf "%BIN%\dex2jar.zip" -C "%BIN%\dex2jar"
  del "%BIN%\dex2jar.zip"
)

echo Done. Engines are in %BIN%
goto :eof

:get
if exist "%~2" ( echo   [ok] %~nx2 already present & goto :eof )
echo   Downloading %~nx2
curl -fL "%~1" -o "%~2"
goto :eof
