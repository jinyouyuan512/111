@echo off
echo ========================================
echo 启动社区服务 (Social Service)
echo ========================================

cd backend\social-service

echo.
echo 正在启动服务...
echo 端口: 8083
echo.

start "social-service" cmd /k "mvn spring-boot:run"

echo.
echo ========================================
echo Social Service 正在启动...
echo 请等待服务完全启动（约30秒）
echo 访问地址: http://localhost:8083
echo API文档: http://localhost:8083/doc.html
echo ========================================

cd ..\..
