@echo off
chcp 65001 >nul
echo.
echo ========================================
echo   强制刷新前端
echo ========================================
echo.

echo 步骤1：停止前端服务...
taskkill /F /FI "WINDOWTITLE eq *npm*" 2>nul
taskkill /F /FI "WINDOWTITLE eq *vite*" 2>nul
timeout /t 2 /nobreak >nul

echo.
echo 步骤2：清除node_modules/.vite缓存...
cd frontend
if exist node_modules\.vite (
    rmdir /s /q node_modules\.vite
    echo ✅ 缓存已清除
) else (
    echo ℹ️  缓存目录不存在
)

echo.
echo 步骤3：重新启动前端...
start "Frontend Dev Server" cmd /k "npm run dev"

echo.
echo ========================================
echo   前端已重启！
echo ========================================
echo.
echo 📋 下一步：
echo 1. 等待前端启动完成（约10秒）
echo 2. 在浏览器中按 Ctrl+Shift+Delete 清除缓存
echo 3. 关闭所有浏览器标签页
echo 4. 重新打开 http://localhost:3001
echo 5. 按 Ctrl+F5 硬刷新页面
echo.
pause
