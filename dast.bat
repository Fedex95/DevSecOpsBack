@echo off
setlocal
set "ZAP_DIR=C:\Program Files\ZAP\Zed Attack Proxy"
set "REPO_DIR=%~1"
if "%REPO_DIR%"=="" set "REPO_DIR=%CD%"

cd /d "%ZAP_DIR%"

set "ZAP_HOME=%TEMP%\zap_home_%RANDOM%"
mkdir "%ZAP_HOME%" 2>nul

powershell -Command "(Get-Content '%REPO_DIR%\policy.yaml') -replace 'TU_IP_EC2', '%ZAP_URL%' | Set-Content '%REPO_DIR%\policy.yaml'"

call zap.bat -cmd ^
  -dir "%ZAP_HOME%" ^
  -autorun "%REPO_DIR%\policy.yaml" ^
  -quickurl %ZAP_URL% ^
  -quickout "%REPO_DIR%\report.html"

exit /b %ERRORLEVEL%