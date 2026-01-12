@echo off
chcp 65001 >nul
echo ========================================
echo 检查创意服务状态
echo ========================================
echo.

echo [1] 检查端口 8087 是否被占用...
netstat -ano | findstr :8087
if %errorlevel% equ 0 (
    echo ✓ 创意服务正在运行
) else (
    echo ✗ 创意服务未运行
    echo.
    echo 请执行以下命令启动服务：
    echo cd backend\creative-service
    echo mvn spring-boot:run
    goto :end
)

echo.
echo [2] 测试服务健康检查...
curl -s http://localhost:8087/actuator/health
echo.

echo.
echo [3] 检查前端代理配置...
echo 前端应该运行在端口 3002
netstat -ano | findstr :3002
if %errorlevel% equ 0 (
    echo ✓ 前端正在运行
) else (
    echo ✗ 前端未运行
)

echo.
echo ========================================
echo 下一步：
echo ========================================
echo 1. 如果服务正在运行，请执行数据库修复 SQL
echo 2. 打开 MySQL，执行 FIX_CREATIVE_1048_ERROR.sql
echo 3. 验证修复：执行 VERIFY_DATABASE_NOW.sql
echo 4. 刷新浏览器测试上传功能
echo ========================================

:end
pause
