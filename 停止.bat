@echo off
title Stop Math Modeling Assistant
netstat -ano | findstr :8080 | findstr LISTENING > tmp.txt
for /f "tokens=5" %a in (tmp.txt) do taskkill //F //PID %a
del tmp.txt
echo Stopped
timeout /t 1 >nul
