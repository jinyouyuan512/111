@echo off
chcp 65001 >nul
echo.
echo ========================================
echo   重启文件上传服务 (Social Service)
echo ========================================
echo.
echo 正在重启服务...
echo.

cd backend\social-service

echo 停止旧服务...
taskkill /F /FI "WINDOWTITLE eq Social Service*" 2>nul
timeout /t 2 /nobreak >nul

echo.
echo 启动新服务...
start "Social Service" cmd /k "mvn spring-boot:run"

echo.
echo ✅ 服务已启动！
echo.
echo 📋 服务信息:
echo   - 端口: 8083
echo   - 文件上传: http://localhost:8083/api/upload
echo   - 静态资源: http://localhost:8083/uploads/
echo.
echo 🔧 已修复的问题:
echo   1. 添加了 @CrossOrigin 注解到 FileUploadController
echo   2. 添加了全局 CORS 配置到 WebMvcConfig
echo   3. 允许跨域访问静态资源
echo.
echo 等待服务启动完成（约30秒）...
timeout /t 30 /nobreak >nul

echo.
echo 🧪 测试服务是否正常...
curl -I http://localhost:8083/api/upload/image 2>nul

echo.
echo ========================================
echo   服务已重启完成！
echo ========================================
echo.
echo 下一步:
echo 1. 等待服务完全启动（查看新窗口的日志）
echo 2. 重新运行测试页面: 测试图片URL.bat
echo 3. 查看图片是否可以正常显示
echo.
pause
