@echo off
chcp 65001 >nul
echo.
echo ========================================
echo   启动 Social Service (文件上传服务)
echo ========================================
echo.

cd backend\social-service

echo 正在启动服务...
echo 端口: 8083
echo.
echo 请等待服务启动完成（约30秒）...
echo 看到 "Started SocialServiceApplication" 表示启动成功
echo.

mvn spring-boot:run

pause
