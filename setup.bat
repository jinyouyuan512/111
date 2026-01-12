@echo off
chcp 65001 >nul
echo 冀忆红途项目初始化脚本
echo ========================
echo.

echo 检查开发环境...

where java >nul 2>nul
if %errorlevel% neq 0 (
    echo ✗ Java 未安装，请安装 JDK 17+
    exit /b 1
) else (
    echo ✓ Java 已安装
)

where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo ✗ Maven 未安装，请安装 Maven 3.8+
    exit /b 1
) else (
    echo ✓ Maven 已安装
)

where node >nul 2>nul
if %errorlevel% neq 0 (
    echo ✗ Node.js 未安装，请安装 Node.js 18+
    exit /b 1
) else (
    echo ✓ Node.js 已安装
)

where npm >nul 2>nul
if %errorlevel% neq 0 (
    echo ✗ npm 未安装
    exit /b 1
) else (
    echo ✓ npm 已安装
)

echo.
echo 环境检查完成！
echo.

echo 启动基础设施服务 (MySQL, Redis, MongoDB, Nacos)...
docker-compose up -d

echo.
echo 等待服务启动...
timeout /t 10 /nobreak >nul

echo.
echo 初始化完成！
echo.
echo 后端启动命令:
echo   cd backend ^&^& mvn clean install
echo   cd backend\gateway ^&^& mvn spring-boot:run
echo.
echo 前端启动命令:
echo   cd frontend ^&^& npm install ^&^& npm run dev
echo.
echo 访问地址:
echo   前端: http://localhost:3000
echo   网关: http://localhost:8080
echo   Nacos: http://localhost:8848/nacos (用户名/密码: nacos/nacos)
echo.
pause
