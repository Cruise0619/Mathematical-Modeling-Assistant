@echo off
title Math Modeling Assistant
cd /d "%~dp0"
java -version >/dev/null 2>&1
if errorlevel 1 (
    echo [ERROR] Java not found
    pause
    exit /b 1
)
if not exist "target\assistant-1.0.0.jar" (
    echo [ERROR] JAR not found
    pause
    exit /b 1
)
echo Starting...
start http://localhost:8080
java -jar target\assistant-1.0.0.jar
