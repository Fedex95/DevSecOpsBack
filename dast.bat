@echo off
setlocal
set "ZAP_DIR=C:\Program Files\ZAP\Zed Attack Proxy"
set "REPO_DIR=%~1"
if "%REPO_DIR%"=="" set "REPO_DIR=%CD%"

cd /d "%ZAP_DIR%"

call zap.bat -cmd -daemon ^
  -autorun "%REPO_DIR%\policy.yaml" ^
  -quickurl %ZAP_URL% ^
  -quickout "%REPO_DIR%\report.html"

exit /b %ERRORLEVEL%