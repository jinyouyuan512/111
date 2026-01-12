@echo off
echo ========================================
echo Redis 快速安装
echo ========================================
echo.
echo 正在启动 PowerShell 脚本...
echo.

powershell -ExecutionPolicy Bypass -File "%~dp0DOWNLOAD_REDIS.ps1"

if %errorlevel% neq 0 (
    echo.
    echo ========================================
    echo 自动安装失败
    echo ========================================
    echo.
    echo 请手动下载 Redis:
    echo https://github.com/tporadowski/redis/releases/download/v5.0.14.1/Redis-x64-5.0.14.1.zip
    echo.
    echo 或查看详细指南: LOGIN_ISSUE_REDIS_FIX.md
    echo.
    pause
)
