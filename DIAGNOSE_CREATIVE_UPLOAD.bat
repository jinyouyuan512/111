@echo off
chcp 65001 >nul
echo ========================================
echo 创意作品上传功能诊断工具
echo ========================================
echo.

echo [1/5] 检查前端服务 (3001)...
netstat -ano | findstr :3001 >nul
if %errorlevel% equ 0 (
    echo ✓ 前端服务正在运行
) else (
    echo ✗ 前端服务未运行
    echo   请运行: cd frontend ^&^& npm run dev
)
echo.

echo [2/5] 检查创意服务 (8087)...
netstat -ano | findstr :8087 >nul
if %errorlevel% equ 0 (
    echo ✓ 创意服务正在运行
) else (
    echo ✗ 创意服务未运行
    echo   请运行: cd backend\creative-service ^&^& mvn spring-boot:run
)
echo.

echo [3/5] 检查社交服务 (8083)...
netstat -ano | findstr :8083 >nul
if %errorlevel% equ 0 (
    echo ✓ 社交服务正在运行 (文件上传)
) else (
    echo ✗ 社交服务未运行
    echo   请运行: cd backend\social-service ^&^& mvn spring-boot:run
)
echo.

echo [4/5] 测试创意服务 API...
curl -s -o nul -w "HTTP状态码: %%{http_code}\n" http://localhost:8087/api/creative/space
echo.

echo [5/5] 测试文件上传服务...
curl -s -o nul -w "HTTP状态码: %%{http_code}\n" http://localhost:8083/api/upload/test
echo.

echo ========================================
echo 诊断完成
echo ========================================
echo.
echo 如果所有服务都在运行，请访问:
echo http://localhost:3001/creative/upload
echo.
echo 测试页面:
echo - test_creative_submit.html (API测试)
echo - test_creative_file_upload.html (文件上传测试)
echo.
pause
