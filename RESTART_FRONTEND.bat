@echo off
chcp 65001 >nul
echo ========================================
echo 重启前端开发服务器
echo ========================================
echo.

cd frontend

echo [1/2] 停止现有服务...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :3001') do (
    echo 发现端口 3001 被占用，PID: %%a
    taskkill /F /PID %%a >nul 2>&1
    echo 已停止进程 %%a
)

echo.
echo [2/2] 启动前端服务...
echo.
echo ========================================
echo 前端服务正在启动...
echo 端口: 3001
echo 访问: http://localhost:3001
echo 按 Ctrl+C 停止服务
echo ========================================
echo.

call npm run dev

pause
