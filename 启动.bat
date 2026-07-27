@echo off
title Math Modeling Assistant
cd /d "%~dp0"

:: Check Java
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java not found. Please install JDK 17+
    pause
    exit /b 1
)

:: Check JAR
if not exist "target\assistant-1.0.0.jar" (
    echo [ERROR] JAR not found. Run: mvn clean package
    pause
    exit /b 1
)

:: Kill existing process on port 8080
echo [INFO] Checking for existing processes on port 8080...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
    echo [INFO] Stopping existing process %%a
    taskkill //F //PID %%a >nul 2>&1
)

:: Wait for port to be released
echo [INFO] Waiting for port 8080 to be released...
set /a counter=0
:wait_loop
timeout /t 1 >nul
set /a counter+=1
netstat -ano | findstr :8080 | findstr LISTENING >nul 2>&1
if not errorlevel 1 (
    if %counter% LSS 10 goto wait_loop
)
netstat -ano | findstr :8080 | findstr LISTENING >nul 2>&1
if not errorlevel 1 (
    echo [WARN] Port 8080 still in use, trying anyway...
) else (
    echo [INFO] Port 8080 is free.
)

:: Clean database lock files
echo [INFO] Cleaning database locks...
if exist "data\shumodb.lock.db" del /f /q "data\shumodb.lock.db" >nul 2>&1
if exist "data\shumodb.mv.db" (
    attrib -r "data\shumodb.mv.db" >nul 2>&1
    del /f /q "data\shumodb.mv.db" >nul 2>&1
)
if exist "data\shumodb.trace.db" del /f /q "data\shumodb.trace.db" >nul 2>&1

:: Start server with console output
echo [INFO] ========================================
echo [INFO]   Math Modeling Assistant
echo [INFO] ========================================
echo [INFO] Starting server on http://localhost:8080
echo [INFO] Press Ctrl+C to stop the server
echo [INFO] ========================================
echo.

java -jar "%~dp0target\assistant-1.0.0.jar"

:: Keep window open after Java exits
echo.
echo [INFO] ========================================
echo [INFO] Server stopped.
echo [INFO] Press any key to exit...
pause >nul
