@echo off
chcp 65001 >nul
echo ========================================
echo   重启 Tourism Service
echo ========================================

cd backend\tourism-service

echo 正在启动 Tourism Service...
start "Tourism Service" cmd /k "mvn spring-boot:run -DskipTests"

echo.
echo Tourism Service 启动中...
echo 端口: 8086
echo.
echo 等待服务启动完成后，访问:
echo http://localhost:3001/tourism
echo.
pause
