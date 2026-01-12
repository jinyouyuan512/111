@echo off
chcp 65001 >nul
echo ========================================
echo 冀忆红途 - 数据库初始化工具
echo ========================================
echo.

set /p MYSQL_USER="请输入MySQL用户名 (默认: root): "
if "%MYSQL_USER%"=="" set MYSQL_USER=root

echo.
echo 请输入MySQL密码:
set /p MYSQL_PASSWORD=

echo.
echo ========================================
echo 开始初始化数据库...
echo ========================================
echo.

echo [1/3] 创建数据库...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% < backend\init-all-databases.sql
if %errorlevel% neq 0 (
    echo 创建数据库失败！请检查MySQL连接。
    pause
    exit /b 1
)
echo ✓ 数据库创建成功

echo.
echo [2/3] 初始化用户服务表结构和数据...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% jiyi_user < backend\user-service\src\main\resources\db\schema.sql
if %errorlevel% neq 0 (
    echo 初始化用户服务失败！
    pause
    exit /b 1
)
echo ✓ 用户服务初始化成功

echo.
echo [3/3] 验证数据库...
echo.
echo 已创建的数据库:
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% -e "SHOW DATABASES LIKE 'jiyi_%%';"

echo.
echo 用户表数据:
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% jiyi_user -e "SELECT id, username, email, nickname, role FROM user;"

echo.
echo ========================================
echo 数据库初始化完成！
echo ========================================
echo.
echo 测试账号:
echo   用户名: testuser
echo   密码: password123
echo.
echo   用户名: admin
echo   密码: password123
echo.
echo 下一步:
echo   1. 启动user-service: START_USER_SERVICE.bat
echo   2. 启动前端: cd frontend ^&^& npm run dev
echo   3. 访问: http://localhost:3000/login
echo.
pause
