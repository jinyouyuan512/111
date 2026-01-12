@echo off
echo ========================================
echo 启动用户服务 (User Service)
echo ========================================
echo.

cd backend

echo 正在编译项目...
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo.
echo 编译成功！正在启动用户服务...
echo 服务将运行在端口 8081
echo.
echo 按 Ctrl+C 停止服务
echo.

call mvn spring-boot:run -pl user-service

pause
