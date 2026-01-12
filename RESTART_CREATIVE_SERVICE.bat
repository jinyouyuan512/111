@echo off
echo ===============================================
echo 重启创意服务 (Creative Service)
echo ===============================================
echo.

echo 正在停止旧的服务...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8087.*LISTENING"') do (
    echo 找到进程 PID: %%a
    taskkill /F /PID %%a
)

echo.
echo 等待 3 秒...
timeout /t 3 /nobreak >nul

echo.
echo 正在启动新的服务...
cd backend\creative-service
start "Creative Service" cmd /k "mvn spring-boot:run"

echo.
echo ===============================================
echo 服务正在启动中...
echo 请等待约 30 秒，然后访问 http://localhost:8087
echo ===============================================
echo.
pause
