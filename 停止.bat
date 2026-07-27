@echo off
title Stop Math Modeling Assistant
cd /d "%~dp0"

echo [INFO] ========================================
echo [INFO]   Stopping Math Modeling Assistant
echo [INFO] ========================================
echo.

:: Method 1: Find processes on port 8080 and kill them
echo [INFO] Method 1: Checking port 8080...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING ^| findstr /v "0.0.0.0"') do (
    echo [INFO] Killing process %%a on port 8080...
    taskkill //F //PID %%a 2>nul
)

:: Wait for port to be released
timeout /t 2 >nul

:: Method 2: Use wmic to find and kill process using port 8080
echo [INFO] Method 2: Using WMIC to find process on port 8080...
for /f "skip=2 tokens=1" %%a in ('wmic process where "name like 'java%%' and commandline like '%%assistant-1.0.0%%'" get processid 2^nul') do (
    if not "%%a"=="" (
        echo [INFO] Killing Java process %%a...
        taskkill //F //PID %%a 2>nul
    )
)

timeout /t 2 >nul

:: Method 3: Direct java.exe kill if still not free
netstat -ano | findstr :8080 | findstr LISTENING >nul 2>&1
if %errorlevel% equ 0 (
    echo [WARN] Port 8080 still in use, using fallback method...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
        taskkill //F //PID %%a 2>nul
    )
    timeout /t 2 >nul
)

:: Verify port is free
netstat -ano | findstr :8080 | findstr LISTENING >nul 2>&1
if %errorlevel% equ 0 (
    echo [WARN] Port 8080 may still be in use by another process
    echo [WARN] You may need to manually end the process or restart your computer
) else (
    echo [INFO] Port 8080 is now free.
)

:: Clean database lock files
echo [INFO] Cleaning database files...
if exist "data\shumodb.lock.db" del /f /q "data\shumodb.lock.db" >nul 2>&1
if exist "data\shumodb.mv.db" (
    attrib -r "data\shumodb.mv.db" >nul 2>&1
    del /f /q "data\shumodb.mv.db" >nul 2>&1
)
if exist "data\shumodb.trace.db" del /f /q "data\shumodb.trace.db" >nul 2>&1

echo.
echo [INFO] ========================================
echo [INFO] Stop script completed.
echo [INFO] ========================================
timeout /t 2 >nul
