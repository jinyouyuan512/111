@echo off
chcp 65001 >nul
echo ========================================
echo ✅ 检查所有服务状态（更新版）
echo ========================================
echo.

echo [1/3] 检查端口占用情况...
echo.
echo 端口 8083 (Social Service):
netstat -ano | findstr :8083
echo.
echo 端口 8087 (Creative Service):
netstat -ano | findstr :8087
echo.
echo 端口 3002 (Frontend - 实际端口):
netstat -ano | findstr :3002
echo.

echo [2/3] 测试后端服务...
echo.
curl -s http://localhost:8083/api/upload/image -X OPTIONS >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Social Service (8083) 正常运行
) else (
    echo ❌ Social Service (8083) 未响应
)

curl -s http://localhost:8087/api/creative/designs?page=1^&size=10 >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Creative Service (8087) 正常运行
) else (
    echo ❌ Creative Service (8087) 未响应
)

echo.
echo [3/3] 测试前端服务...
echo.
curl -s http://localhost:3002 >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Frontend (3002) 正常运行
) else (
    echo ❌ Frontend (3002) 未响应
)

echo.
echo ========================================
echo 📌 服务地址（已更新）：
echo ========================================
echo   ✅ Social Service:   http://localhost:8083
echo   ✅ Creative Service: http://localhost:8087
echo   ✅ Frontend:         http://localhost:3002  ⚠️ 注意端口变更
echo.
echo ⚠️  重要提示：
echo   前端端口从 3001 变更为 3002
echo   原因：端口 3001 被占用，Vite 自动切换
echo.
echo 📌 访问前端：
echo   http://localhost:3002
echo.
echo 📌 测试图片访问（替换为实际文件名）：
echo   http://localhost:8083/uploads/images/2026/01/04/xxx.png
echo.
echo ========================================
echo.
pause
