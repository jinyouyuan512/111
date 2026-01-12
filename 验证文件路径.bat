@echo off
chcp 65001 >nul
echo.
echo ========================================
echo   验证文件路径和访问
echo ========================================
echo.

echo 1. 检查文件是否存在...
echo.
if exist "backend\social-service\uploads\images\2026\01\04\36b56a91-ba57-403d-acd5-6d6d6805e41c.png" (
    echo ✅ 文件存在！
    echo 路径: backend\social-service\uploads\images\2026\01\04\36b56a91-ba57-403d-acd5-6d6d6805e41c.png
) else (
    echo ❌ 文件不存在！
)

echo.
echo 2. 检查上传目录结构...
echo.
dir /s /b backend\social-service\uploads\images\2026\01\04\

echo.
echo 3. 检查服务端口...
echo.
netstat -ano | findstr :8083

echo.
echo 4. 测试URL访问...
echo.
echo 正在测试: http://localhost:8083/uploads/images/2026/01/04/36b56a91-ba57-403d-acd5-6d6d6805e41c.png
echo.
curl -I http://localhost:8083/uploads/images/2026/01/04/36b56a91-ba57-403d-acd5-6d6d6805e41c.png

echo.
echo ========================================
echo   诊断完成
echo ========================================
echo.
pause
