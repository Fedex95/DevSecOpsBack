@echo off
setlocal
set "ZAP_DIR=C:\Program Files\ZAP\Zed Attack Proxy"
set "REPO_DIR=%~1"
if "%REPO_DIR%"=="" set "REPO_DIR=%CD%"

cd /d "%ZAP_DIR%"

set "ZAP_HOME=%TEMP%\zap_home_%RANDOM%"
mkdir "%ZAP_HOME%" 2>nul

set "HOSTPORT=%ZAP_URL:http://=%"
set "HOSTPORT=%HOSTPORT:https://=%"

set "REPO_SAFE=%REPO_DIR:\=/%"

set "PLAN=%TEMP%\zap_plan.yaml"
copy "%REPO_DIR%\policy.yaml" "%PLAN%" >nul

powershell -NoProfile -Command "$p='%PLAN%';$c=Get-Content -Raw -Path $p; $c=$c.Replace('FULL_URL','%ZAP_URL%').Replace('HOSTPORT','%HOSTPORT%').Replace('OPENAPI_URL','%ZAP_URL%/v3/api-docs').Replace('REPORT_DIR','%REPO_SAFE%'); Set-Content -Path $p -Value $c"

call zap.bat -cmd -dir "%ZAP_HOME%" -autorun "%PLAN%"

exit /b %ERRORLEVEL%