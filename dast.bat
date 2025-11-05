@echo off
setlocal
set "ZAP_DIR=C:\Program Files\ZAP\Zed Attack Proxy"
set "REPO_DIR=%~1"
if "%REPO_DIR%"=="" set "REPO_DIR=%CD%"

cd /d "%ZAP_DIR%"

set "ZAP_HOME=%TEMP%\zap_home_%RANDOM%"
mkdir "%ZAP_HOME%" 2>nul

set "PLAN=%TEMP%\zap_plan.yaml"
copy "%REPO_DIR%\policy.yaml" "%PLAN%" >nul

set "REPO_SAFE=%REPO_DIR:\=/%"

powershell -NoProfile -Command "$p='%PLAN%';$c=[IO.File]::ReadAllText($p);$repo='%REPO_SAFE%';$url=$env:ZAP_URL;$hostport=($url -replace '^https?://','');$openapi=$url + '/v3/api-docs';$c=$c.Replace('FULL_URL',$url).Replace('HOSTPORT',$hostport).Replace('OPENAPI_URL',$openapi).Replace('REPORT_DIR',$repo);[IO.File]::WriteAllText($p,$c)"

call zap.bat -cmd -dir "%ZAP_HOME%" -autorun "%PLAN%"
exit /b %ERRORLEVEL%