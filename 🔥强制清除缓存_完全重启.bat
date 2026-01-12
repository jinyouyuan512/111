@echo off
chcp 65001 >nul
echo ========================================
echo 🔥 强制清除所有缓存并重启前端
echo ========================================
echo.

echo [1/5] 停止前端服务...
taskkill /F /IM node.exe 2>nul
timeout /t 2 >nul

echo [2/5] 删除 Vite 缓存...
if exist "frontend\node_modules\.vite" (
    rmdir /s /q "frontend\node_modules\.vite"
    echo ✅ Vite 缓存已删除
) else (
    echo ⚠️ Vite 缓存目录不存在
)

echo [3/5] 删除 dist 目录...
if exist "frontend\dist" (
    rmdir /s /q "frontend\dist"
    echo ✅ dist 目录已删除
) else (
    echo ⚠️ dist 目录不存在
)

echo [4/5] 等待 3 秒...
timeout /t 3 >nul

echo [5/5] 重新启动前端服务...
cd frontend
start cmd /k "npm run dev"

echo.
echo ========================================
echo ✅ 前端服务已重启
echo ========================================
echo.
echo 📌 接下来请执行以下步骤：
echo.
echo 1. 等待前端服务完全启动（看到 "Local: http://localhost:3001"）
echo 2. 打开浏览器
echo 3. 按 Ctrl+Shift+Delete 清除浏览器缓存
echo 4. 关闭所有浏览器标签页
echo 5. 重新打开 http://localhost:3001
echo 6. 按 Ctrl+F5 硬刷新页面
echo 7. 打开众创空间，点击任意作品查看详情
echo.
echo ⚠️ 如果仍然显示黑色区域，请查看浏览器控制台的错误信息
echo.
pause
