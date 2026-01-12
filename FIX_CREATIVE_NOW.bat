@echo off
chcp 65001 >nul
echo ========================================
echo 修复创意上传 1048 错误
echo ========================================
echo.

echo [步骤 1/3] 修复数据库字段约束...
echo.
echo 请在 MySQL 中执行以下 SQL 文件:
echo FIX_CREATIVE_1048_ERROR.sql
echo.
echo 或者手动执行:
echo mysql -u root -p jiyi_creative ^< FIX_CREATIVE_1048_ERROR.sql
echo.
pause

echo.
echo [步骤 2/3] 重启创意服务...
echo.
echo 正在查找创意服务进程...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8087') do (
    echo 找到进程 PID: %%a
    taskkill /F /PID %%a
)
echo.
echo 请手动重启创意服务:
echo cd backend\creative-service
echo mvn spring-boot:run
echo.
pause

echo.
echo [步骤 3/3] 测试修复...
echo.
echo 打开测试页面...
start check_creative_error.html
echo.
echo 请在测试页面中:
echo 1. 确认端口是 3002
echo 2. 点击"测试最小请求"
echo 3. 查看是否成功
echo.
echo ========================================
echo 修复完成
echo ========================================
echo.
echo 如果仍有问题，请查看:
echo - 后端日志中的详细错误
echo - 数据库字段约束是否正确
echo - 创意服务是否成功重启
echo.
pause
