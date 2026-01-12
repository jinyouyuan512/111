@echo off
chcp 65001 >nul
echo ========================================
echo 🚀 启动前端服务
echo ========================================
echo.

echo [1/3] 检查 Node.js 是否安装...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Node.js 未安装或未添加到 PATH
    echo 请先安装 Node.js: https://nodejs.org/
    pause
    exit /b 1
)
echo ✅ Node.js 已安装

echo.
echo [2/3] 进入前端目录...
cd frontend
if %errorlevel% neq 0 (
    echo ❌ 无法进入 frontend 目录
    pause
    exit /b 1
)
echo ✅ 已进入 frontend 目录

echo.
echo [3/3] 启动前端服务...
echo.
echo 📌 前端服务将运行在: http://localhost:3001
echo 📌 按 Ctrl+C 可以停止服务
echo.
echo ========================================
echo.

npm run dev

pause
