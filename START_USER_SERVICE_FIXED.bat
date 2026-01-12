@echo off
echo ========================================
echo 启动用户服务 (User Service)
echo ========================================
echo.

echo 检查 MySQL 是否运行...
netstat -an | findstr "3306" >nul
if errorlevel 1 (
    echo [错误] MySQL 未运行！请先启动 MySQL
    echo 提示: 运行 INIT_DATABASE.bat 来初始化数据库
    pause
    exit /b 1
)
echo [成功] MySQL 正在运行

echo.
echo 检查 Redis 是否运行...
netstat -an | findstr "6379" >nul
if errorlevel 1 (
    echo [警告] Redis 未运行！
    echo 提示: 请先启动 Redis 服务
    pause
    exit /b 1
)
echo [成功] Redis 正在运行

echo.
echo 正在启动 User Service (端口 8081)...
cd backend\user-service
mvn spring-boot:run

pause
