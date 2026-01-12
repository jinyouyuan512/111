@echo off
chcp 65001 >nul
echo ========================================
echo 冀忆红途 - Redis 快速启动
echo ========================================
echo.

REM 检查Redis是否已经在运行
netstat -ano | findstr ":6379" >nul 2>&1
if %errorlevel% == 0 (
    echo [✓] Redis 已经在运行
    echo.
    pause
    exit /b 0
)

echo [!] Redis 未运行
echo.
echo 正在检查Redis安装...
echo.

REM 检查常见的Redis安装位置
set REDIS_PATH=
if exist "C:\Redis\redis-server.exe" set REDIS_PATH=C:\Redis
if exist "C:\Program Files\Redis\redis-server.exe" set REDIS_PATH=C:\Program Files\Redis
if exist "%USERPROFILE%\Redis\redis-server.exe" set REDIS_PATH=%USERPROFILE%\Redis
if exist "redis-server.exe" set REDIS_PATH=.

if "%REDIS_PATH%"=="" (
    echo [✗] 未找到Redis安装
    echo.
    echo 请按照以下步骤安装Redis:
    echo.
    echo 1. 访问: https://github.com/tporadowski/redis/releases
    echo 2. 下载: Redis-x64-5.0.14.1.zip
    echo 3. 解压到: C:\Redis
    echo 4. 重新运行此脚本
    echo.
    echo 或者使用Docker:
    echo    docker run -d --name jiyi-redis -p 6379:6379 redis
    echo.
    pause
    exit /b 1
)

echo [✓] 找到Redis: %REDIS_PATH%
echo.
echo 正在启动Redis服务器...
echo.

cd /d "%REDIS_PATH%"
start "Redis Server" redis-server.exe

timeout /t 2 >nul

REM 验证Redis是否启动成功
netstat -ano | findstr ":6379" >nul 2>&1
if %errorlevel% == 0 (
    echo [✓] Redis 启动成功！
    echo.
    echo Redis 正在运行在端口 6379
    echo.
) else (
    echo [✗] Redis 启动失败
    echo.
    echo 请手动启动Redis:
    echo    cd %REDIS_PATH%
    echo    redis-server.exe
    echo.
)

pause
