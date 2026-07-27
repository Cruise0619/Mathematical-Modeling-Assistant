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
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
    taskkill //F //PID %%a >nul 2>&1
)
timeout /t 1 >nul

:: Clean database locks
if exist "data\shumodb.lock.db" del "data\shumodb.lock.db" >nul 2>&1
if exist "data\shumodb.mv.db" (
    attrib -r "data\shumodb.mv.db" >nul 2>&1
)

:: Start server
echo [INFO] Starting Math Modeling Assistant...
start http://localhost:8080
start /min java -jar "%~dp0target\assistant-1.0.0.jar"
timeout /t 3 >nul
echo [OK] Server started. Visit http://localhost:8080
pause
