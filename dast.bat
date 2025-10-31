@echo off
"C:\Program Files\ZAP\Zed Attack Proxy\zap.bat" -cmd -autorun policy.yaml -quickurl %ZAP_URL% -quickout report.html -daemon