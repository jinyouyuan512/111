@echo off
echo ========================================
echo 重启 Mall Service
echo ========================================

echo.
echo 正在停止 mall-service...
cd backend\mall-service
taskkill /F /FI "WINDOWTITLE eq mall-service*" 2>nul

echo.
echo 等待3秒...
timeout /t 3 /nobreak >nul

echo.
echo 正在启动 mall-service...
start "mall-service" cmd /k "mvn spring-boot:run"

echo.
echo ========================================
echo Mall Service 已重启
echo 请等待服务完全启动（约30秒）
echo ========================================
