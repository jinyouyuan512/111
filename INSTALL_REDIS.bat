@echo off
echo ========================================
echo Redis Installation for Windows
echo ========================================
echo.

echo This script will help you download Redis for Windows
echo.
echo Option 1: Download Redis manually
echo   URL: https://github.com/tporadowski/redis/releases/download/v5.0.14.1/Redis-x64-5.0.14.1.zip
echo   1. Download the ZIP file (about 5MB)
echo   2. Extract to C:\Redis (or any folder)
echo   3. Run: C:\Redis\redis-server.exe
echo.
echo Option 2: Use Docker (if installed)
echo   docker run -d --name jiyi-redis -p 6379:6379 redis:latest
echo.
echo Option 3: Use Chocolatey (if installed)
echo   choco install redis-64
echo.

echo ========================================
echo Quick Check
echo ========================================
echo.

echo Checking if Redis is already running...
netstat -ano | findstr ":6379 " >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Redis appears to be running on port 6379
    echo.
    pause
    exit /b 0
) else (
    echo [X] Redis is NOT running on port 6379
)

echo.
echo Checking if redis-server.exe exists in common locations...

if exist "C:\Redis\redis-server.exe" (
    echo [FOUND] C:\Redis\redis-server.exe
    echo.
    echo Starting Redis...
    start "Redis Server" "C:\Redis\redis-server.exe"
    timeout /t 3 >nul
    echo Redis started! Check the new window.
    echo.
    pause
    exit /b 0
)

if exist "redis-server.exe" (
    echo [FOUND] redis-server.exe in current directory
    echo.
    echo Starting Redis...
    start "Redis Server" redis-server.exe
    timeout /t 3 >nul
    echo Redis started! Check the new window.
    echo.
    pause
    exit /b 0
)

echo.
echo [NOT FOUND] Redis is not installed
echo.
echo ========================================
echo Installation Instructions
echo ========================================
echo.
echo Please follow these steps:
echo.
echo 1. Open this URL in your browser:
echo    https://github.com/tporadowski/redis/releases/download/v5.0.14.1/Redis-x64-5.0.14.1.zip
echo.
echo 2. Download the ZIP file (5MB)
echo.
echo 3. Extract to C:\Redis
echo.
echo 4. Run this script again, or manually run:
echo    C:\Redis\redis-server.exe
echo.
echo 5. After Redis starts, the User Service will automatically connect
echo.

pause
