@echo off
chcp 65001 >nul
echo ========================================
echo    众创空间页面 - 清除缓存并打开
echo ========================================
echo.
echo 正在执行以下操作:
echo 1. 打开测试页面验证 API
echo 2. 打开众创空间页面 (使用无缓存模式)
echo.
echo ========================================
echo.

echo [1/2] 打开 API 测试页面...
start "" "test_creative_works_display.html"
timeout /t 2 /nobreak >nul

echo [2/2] 打开众创空间页面 (Chrome 无缓存模式)...
echo.
echo 提示: 如果页面没有自动打开，请手动访问:
echo   http://localhost:3001/creative
echo.
echo 清除浏览器缓存的方法:
echo   1. 按 Ctrl+Shift+Delete 打开清除浏览器数据
echo   2. 选择 "缓存的图片和文件"
echo   3. 点击 "清除数据"
echo   4. 或者使用无痕模式: Ctrl+Shift+N (Chrome) 或 Ctrl+Shift+P (Firefox)
echo.

REM 尝试使用 Chrome 无缓存模式打开
start chrome --disable-cache --disable-application-cache --incognito "http://localhost:3001/creative" 2>nul

if errorlevel 1 (
    echo Chrome 未找到，尝试使用默认浏览器...
    start "" "http://localhost:3001/creative"
)

echo.
echo ========================================
echo 完成！
echo.
echo 请检查:
echo 1. 测试页面显示的作品数量
echo 2. 众创空间页面右上角的版本号: 2025-01-04 15:30
echo 3. 众创空间页面显示的作品数量
echo.
echo 如果看不到版本号或作品数量为 0，请:
echo - 按 F12 打开开发者工具
echo - 查看 Console 标签页
echo - 查找 "=== 众创空间页面已加载" 的日志
echo ========================================
pause
