@echo off
title Stop Math Modeling Assistant

echo [INFO] Stopping Math Modeling Assistant...

:: Kill processes on port 8080
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
    echo [INFO] Stopping process %%a
    taskkill //F //PID %%a >nul 2>&1
)

:: Clean lock files
if exist "data\shumodb.lock.db" del "data\shumodb.lock.db" >nul 2>&1

echo [OK] Server stopped
timeout /t 2 >nul
