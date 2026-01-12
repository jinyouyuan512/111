@echo off
chcp 65001 >nul
echo ========================================
echo ✅ 检查所有服务状态
echo ========================================
echo.

echo [1/4] 检查端口占用情况...
echo.
echo 端口 8083 (Social Service):
netstat -ano | findstr :8083
echo.
echo 端口 8087 (Creative Service):
netstat -ano | findstr :8087
echo.
echo 端口 3001 (Frontend):
netstat -ano | findstr :3001
echo.

echo [2/4] 测试 Social Service (8083)...
curl -s http://localhost:8083/api/upload/image -X OPTIONS >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Social Service 正常运行
) else (
    echo ❌ Social Service 未响应
)

echo.
echo [3/4] 测试 Creative Service (8087)...
curl -s http://localhost:8087/api/creative/designs?page=1^&size=10 >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Creative Service 正常运行
) else (
    echo ❌ Creative Service 未响应
)

echo.
echo [4/4] 测试前端服务 (3001)...
curl -s http://localhost:3001 >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Frontend 正常运行
) else (
    echo ❌ Frontend 未响应
)

echo.
echo ========================================
echo 📌 服务地址：
echo   - Social Service:   http://localhost:8083
echo   - Creative Service: http://localhost:8087
echo   - Frontend:         http://localhost:3001
echo.
echo 📌 测试图片访问（替换为实际文件名）：
echo   http://localhost:8083/uploads/images/2026/01/04/xxx.png
echo.
echo 📌 访问前端：
echo   http://localhost:3001
echo ========================================
echo.
pause
