@echo off
chcp 65001 >nul
echo ========================================
echo 启动传承学院服务 (Academy Service)
echo ========================================
echo.

echo 检查 MySQL 是否运行...
netstat -ano | findstr "3306" >nul
if %errorlevel%==0 (
    echo ✓ MySQL 正在运行
) else (
    echo ✗ MySQL 未运行，请先启动 MySQL
    pause
    exit /b 1
)

echo.
echo 启动 Academy Service (端口 8088)...
echo.
cd backend\academy-service
call mvn spring-boot:run

pause
