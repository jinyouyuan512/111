@echo off
chcp 65001 >nul
echo ========================================
echo 查看创意服务后端日志
echo ========================================
echo.
echo 这个窗口会显示后端的实时日志
echo 当你提交作品时，可以看到详细的 SQL 语句
echo.
echo 按 Ctrl+C 退出
echo.
pause
echo.

cd backend\creative-service
mvn spring-boot:run
