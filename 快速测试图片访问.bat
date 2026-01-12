@echo off
chcp 65001 >nul
echo.
echo ========================================
echo   快速测试图片访问
echo ========================================
echo.

echo 测试图片URL...
echo.
curl -I http://localhost:8083/uploads/images/2026/01/04/36b56a91-ba57-403d-acd5-6d6d6805e41c.png

echo.
echo.
echo 如果看到 "HTTP/1.1 200" 表示成功！
echo 如果看到 "HTTP/1.1 404" 表示文件不存在或路径错误
echo.
echo 现在打开测试页面...
start test_image_url_access.html

echo.
pause
