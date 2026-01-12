@echo off
chcp 65001 >nul
echo.
echo ========================================
echo   测试图片URL访问
echo ========================================
echo.
echo 正在打开测试页面...
echo.
start test_image_url_access.html
echo.
echo 测试页面已打开！
echo.
echo 📋 测试步骤:
echo 1. 查看页面中的图片是否显示
echo 2. 打开浏览器开发者工具 (F12)
echo 3. 切换到 Network 标签
echo 4. 刷新页面 (F5)
echo 5. 查看图片请求的状态码
echo.
echo 状态码说明:
echo   200 = 成功 (图片存在且可访问)
echo   404 = 文件不存在
echo   500 = 服务器错误
echo   ERR_CONNECTION_REFUSED = 后端服务未启动
echo.
pause
