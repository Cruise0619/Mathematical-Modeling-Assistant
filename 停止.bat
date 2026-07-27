@echo off
title Stop Math Modeling Assistant
cd /d "%~dp0"

echo [INFO] ========================================
echo [INFO]   Stopping Math Modeling Assistant
echo [INFO] ========================================
echo.

:: Kill processes on port 8080
echo [INFO] Finding processes on port 8080...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
    echo [INFO] Killing process %%a...
    taskkill //F //PID %%a >nul 2>&1
)

:: Wait a moment
timeout /t 1 >nul

:: Verify port is free
netstat -ano | findstr :8080 | findstr LISTENING >nul 2>&1
if %errorlevel% equ 0 (
    echo [WARN] Port 8080 still in use, force killing...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
        taskkill //F //PID %%a >nul 2>&1
    )
    timeout /t 1 >nul
)

:: Clean database lock files
echo [INFO] Cleaning database files...
if exist "data\shumodb.lock.db" del /f /q "data\shumodb.lock.db" >nul 2>&1
if exist "data\shumodb.mv.db" del /f /q "data\shumodb.mv.db" >nul 2>&1
if exist "data\shumodb.trace.db" del /f /q "data\shumodb.trace.db" >nul 2>&1

echo.
echo [INFO] ========================================
echo [INFO] Server stopped.
echo [INFO] ========================================
timeout /t 2 >nul
