@echo off
chcp 65001 >nul
echo ========================================
echo 自动修复数据库 - Error 1048
echo ========================================
echo.
echo 这个脚本将尝试自动修复数据库问题
echo.
echo 请确保：
echo 1. MySQL 正在运行
echo 2. 你知道 MySQL 的 root 密码
echo.
pause
echo.

set /p MYSQL_PASSWORD="请输入 MySQL root 密码: "

echo.
echo 正在连接 MySQL 并执行修复...
echo.

mysql -u root -p%MYSQL_PASSWORD% jiyi_creative < 一键修复.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo ✓ 修复成功！
    echo ========================================
    echo.
    echo 现在请：
    echo 1. 刷新浏览器 ^(Ctrl+F5^)
    echo 2. 访问 http://localhost:3002/creative/upload
    echo 3. 测试上传功能
    echo.
) else (
    echo.
    echo ========================================
    echo ✗ 修复失败
    echo ========================================
    echo.
    echo 可能的原因：
    echo 1. MySQL 密码错误
    echo 2. MySQL 未运行
    echo 3. 数据库 jiyi_creative 不存在
    echo.
    echo 请手动在 MySQL 中执行 一键修复.sql 文件
    echo.
)

pause
