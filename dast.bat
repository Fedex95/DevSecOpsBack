@echo off
setlocal
set "ZAP_DIR=C:\Program Files\ZAP\Zed Attack Proxy"
set "REPO_DIR=%~1"
if "%REPO_DIR%"=="" set "REPO_DIR=%CD%"

cd /d "%ZAP_DIR%"

set "ZAP_HOME=%TEMP%\zap_home_%RANDOM%"
mkdir "%ZAP_HOME%" 2>nul

REM Copia plan base
set "PLAN=%TEMP%\zap_plan.yaml"
copy "%REPO_DIR%\policy.yaml" "%PLAN%" >nul

REM Build de reemplazos vía PowerShell usando variables de entorno 
powershell -NoProfile -Command ^
  "$p = '%PLAN%';" ^
  "$c = Get-Content -Raw -Path $p;" ^
  "$repo = '%REPO_DIR%'.Replace('\','/');" ^
  "$url = $env:ZAP_URL;" ^
  "$hostport = ($url -replace '^https?://','');" ^
  "$openapi = $url + '/v3/api-docs';" ^
  "$loginPage = $env:DAST_LOGIN_PAGE_URL;" ^
  "$loginUrl   = $env:DAST_LOGIN_URL;" ^
  "$loginBody  = $env:DAST_LOGIN_REQUEST_BODY;" ^
  "$userName   = $env:DAST_USERNAME;" ^
  "$userPass   = $env:DAST_PASSWORD;" ^
  "$loggedIn   = $env:DAST_LOGGED_IN_INDICATOR;" ^
  "$wordlist   = $env:DAST_WORDLIST;" ^
  "$c = $c.Replace('FULL_URL', $url)" ^
         ".Replace('HOSTPORT', $hostport)" ^
         ".Replace('OPENAPI_URL', $openapi)" ^
         ".Replace('REPORT_DIR', $repo)" ^
         ".Replace('LOGIN_PAGE_URL', $loginPage)" ^
         ".Replace('LOGIN_URL', $loginUrl)" ^
         ".Replace('LOGIN_REQUEST_BODY', $loginBody)" ^
         ".Replace('AUTH_USERNAME', $userName)" ^
         ".Replace('AUTH_PASSWORD', $userPass)" ^
         ".Replace('LOGGED_IN_INDICATOR', $loggedIn)" ^
         ".Replace('WORDLIST', $wordlist);" ^
  "Set-Content -Path $p -Value $c;"

REM Ejecuta ZAP con el plan (report.html y report.json quedan en el repo)
call zap.bat -cmd -dir "%ZAP_HOME%" -autorun "%PLAN%"

exit /b %ERRORLEVEL%