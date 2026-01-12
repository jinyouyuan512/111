@echo off
chcp 65001 >nul
echo ========================================
echo 🔄 强制刷新前端测试
echo ========================================
echo.
echo 请按以下步骤操作：
echo.
echo 1. 在浏览器中按 Ctrl+Shift+Delete 清除缓存
echo 2. 选择"所有时间"
echo 3. 勾选"缓存的图片和文件"
echo 4. 点击"清除数据"
echo 5. 按 Ctrl+Shift+R 强制刷新页面
echo.
echo 或者直接打开以下链接（带时间戳防止缓存）：
echo.

set timestamp=%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%
set timestamp=%timestamp: =0%

echo http://localhost:3002/creative?_t=%timestamp%
echo.
echo ========================================
echo 正在打开浏览器...
echo ========================================

start "" "http://localhost:3002/creative?_t=%timestamp%"

echo.
echo 如果图片仍然不显示，请打开浏览器开发者工具（F12）
echo 查看 Console 标签中的日志输出
echo.
echo 应该看到类似以下的日志：
echo   === 开始加载作品 (版本 2026-01-04-20:00) ===
echo   ✅ 使用新格式 (records)，作品数量: X
echo   === 处理作品数据 ===
echo   design.coverImage: http://localhost:8083/uploads/...
echo.
pause
