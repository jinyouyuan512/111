@echo off
chcp 65001 >nul
echo ========================================
echo 🎨 作品详情媒体显示测试工具
echo ========================================
echo.

echo [1/3] 检查前端服务...
timeout /t 2 >nul

echo.
echo [2/3] 打开诊断工具...
start http://localhost:5173/test_creative_detail_media.html
timeout /t 2 >nul

echo.
echo [3/3] 打开众创空间页面...
start http://localhost:5173/creative
timeout /t 2 >nul

echo.
echo ========================================
echo ✅ 测试页面已打开
echo ========================================
echo.
echo 📋 测试步骤：
echo.
echo 1. 在诊断工具中点击"加载所有作品"
echo 2. 查看你上传的作品数据
echo 3. 在众创空间页面点击作品查看详情
echo 4. 确认图片/视频正常显示
echo.
echo 💡 提示：
echo - 打开浏览器控制台(F12)查看详细日志
echo - 如果看不到媒体，检查 coverImage 和 files 字段
echo - 使用诊断工具的"测试媒体URL"功能验证URL
echo.
echo ========================================
echo.
pause
